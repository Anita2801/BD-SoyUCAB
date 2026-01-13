const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getPostsByAuthor } = require('../db');
const { ChartJSNodeCanvas } = require('chartjs-node-canvas');

const JSREPORT_URL = 'http://localhost:5489/api/report';
const TARGET_USER = 'mcervantes.02'; // Example user

const width = 400;
const height = 400;
const canvas = new ChartJSNodeCanvas({ width, height });

async function generateChart(data) {
    // Truncate labels for chart (using Date + snippet)
    const labels = data.map(d => {
        const date = new Date(d.fecha_publicacion).toLocaleDateString();
        const text = d.cuerpo_post || '';
        const snippet = text.length > 10 ? text.substring(0, 10) + '...' : text;
        return `${date} - ${snippet}`;
    });

    const config = {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Me Gusta',
                    data: data.map(d => parseInt(d.me_gusta)),
                    backgroundColor: '#2ecc71', // Green
                },
                {
                    label: 'No Me Gusta',
                    data: data.map(d => parseInt(d.no_me_gusta)),
                    backgroundColor: '#e74c3c', // Red
                }
            ]
        },
        options: {
            indexAxis: 'y', // Horizontal bar chart better for long labels
            plugins: {
                legend: { position: 'bottom' },
                title: { display: false }
            },
            responsive: false,
            scales: {
                x: { stacked: true },
                y: { stacked: true }
            }
        }
    };
    return await canvas.renderToBuffer(config);
}


async function generateReport() {
    console.log(`Fetching posts for user: "${TARGET_USER}"...`);
    const posts = await getPostsByAuthor(TARGET_USER);
    console.log(`Found ${posts.length} posts.`);

    if (posts.length === 0) {
        console.log("No posts found for this user.");
        return;
    }

    const fullname = posts[0].autor_nombre_completo || 'Unknown';

    // Prepare HTML rows
    const rowsHtml = posts.map(p => {
        const date = new Date(p.fecha_publicacion).toLocaleString();
        return `
        <tr>
            <td>${date}</td>
            <td>
                <div style="color: green;">👍 ${p.me_gusta}</div>
                <div style="color: red;">MNG ${p.no_me_gusta}</div>
            </td>
            <td>${p.cuerpo_post}</td>
        </tr>
        `;
    }).join('');

    // Generate Chart
    console.log('Generating Chart...');
    let chartBase64 = '';
    try {
        const chartBuffer = await generateChart(posts);
        chartBase64 = `data:image/png;base64,${chartBuffer.toString('base64')}`;
    } catch (err) {
        console.error("Chart generation failed:", err);
    }

    // Read Template
    let html = fs.readFileSync(path.join(__dirname, '../templates', 'posts_activity.html'), 'utf-8');

    // Inject Data
    html = html.replace('{{username}}', TARGET_USER);
    html = html.replace('{{fullname}}', fullname);
    html = html.replace('{{rows}}', rowsHtml);
    html = html.replace('{{chartImage}}', chartBase64);

    console.log('Sending to JSReports...');
    try {
        const response = await axios.post(JSREPORT_URL, {
            template: {
                content: html,
                engine: 'none',
                recipe: 'chrome-pdf'
            }
        }, {
            responseType: 'arraybuffer'
        });

        fs.writeFileSync(path.join(__dirname, '../pdfs', 'posts_activity_report.pdf'), response.data);
        console.log('Success! Report saved to reports/pdfs/posts_activity_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

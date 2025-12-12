const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { searchContent } = require('../db');
const { ChartJSNodeCanvas } = require('chartjs-node-canvas');

const JSREPORT_URL = 'http://localhost:5489/api/report';
const SEARCH_TERM = 'SQL'; // Example search term

const width = 400;
const height = 400;
const canvas = new ChartJSNodeCanvas({ width, height });

async function generateChart(data) {
    // Truncate labels for chart
    const labels = data.map(d => {
        const text = d.cuerpo_post || '';
        return text.length > 15 ? text.substring(0, 15) + '...' : text;
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
            plugins: {
                legend: { position: 'bottom' },
                title: { display: true, text: 'Interacciones' }
            },
            responsive: false,
            scales: {
                x: { stacked: true },
                y: { stacked: true, beginAtZero: true }
            }
        }
    };
    return await canvas.renderToBuffer(config);
}


async function generateReport() {
    console.log(`Searching content for term: "${SEARCH_TERM}"...`);
    const results = await searchContent(SEARCH_TERM);
    console.log(`Found ${results.length} results.`);

    // Prepare HTML rows
    const rowsHtml = results.map(r => {
        const date = new Date(r.fecha_publicacion).toLocaleDateString();
        return `
        <tr>
            <td>
                <div>${r.autor_usuario}</div>
                <div class="meta">${r.autor_nombre_completo}</div>
            </td>
            <td>${date}</td>
            <td>
                <div style="color: green;">👍 ${r.me_gusta}</div>
                <div style="color: red;">MNG ${r.no_me_gusta}</div>
            </td>
            <td>${r.cuerpo_post}</td>
        </tr>
        `;
    }).join('');

    // Generate Chart
    console.log('Generating Chart...');
    let chartBase64 = '';
    try {
        if (results.length > 0) {
            const chartBuffer = await generateChart(results);
            chartBase64 = `data:image/png;base64,${chartBuffer.toString('base64')}`;
        }
    } catch (err) {
        console.error("Chart generation failed:", err);
    }

    // Read Template
    let html = fs.readFileSync(path.join(__dirname, '../templates', 'search_content.html'), 'utf-8');

    // Inject Data
    html = html.replace('{{searchTerm}}', SEARCH_TERM);
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

        fs.writeFileSync(path.join(__dirname, '../../reports', 'search_content_report.pdf'), response.data);
        console.log('Success! Report saved to reports/search_content_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

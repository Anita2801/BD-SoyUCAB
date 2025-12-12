const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getMetrics } = require('../db');
const { ChartJSNodeCanvas } = require('chartjs-node-canvas');

const JSREPORT_URL = 'http://localhost:5489/api/report';

// Chart Service Configuration
// Define a plugin to paint the background white
const whiteBackgroundPlugin = {
    id: 'whiteBackground',
    beforeDraw: (chart) => {
        const ctx = chart.ctx;
        ctx.save();
        ctx.fillStyle = 'white';
        ctx.fillRect(0, 0, chart.width, chart.height);
        ctx.restore();
    }
};

const width = 800; // px
const height = 400; // px
const chartCallback = (ChartJS) => {
    ChartJS.defaults.responsive = false;
    ChartJS.defaults.maintainAspectRatio = false;
    ChartJS.register(whiteBackgroundPlugin); // Register global background plugin
};
const chartJSNodeCanvas = new ChartJSNodeCanvas({ width, height, chartCallback });

// Keep this function for the donut chart
async function generateChartImage(configuration) {
    return await chartJSNodeCanvas.renderToBuffer(configuration);
}

async function generateReport() {
    console.log('Fetching metrics from database...');
    const metrics = await getMetrics();
    console.log('Metrics fetched successfully.');

    // --- Generate Charts Server-Side ---
    console.log('Generating charts server-side...');



    // 2. Type Doughnut Chart
    const typeConfig = {
        type: 'doughnut',
        data: {
            labels: metrics.distribution.map(d => d.tipo_rol),
            datasets: [{
                data: metrics.distribution.map(d => d.count),
                backgroundColor: ['#f1c40f', '#2ecc71', '#e74c3c', '#9b59b6']
            }]
        },
        options: {
            plugins: {
                legend: { labels: { font: { size: 20 } } }
            }
        }
    };
    const typeBuffer = await generateChartImage(typeConfig);
    const typeBase64 = `data:image/png;base64,${typeBuffer.toString('base64')}`;


    // --- Prepare HTML ---
    console.log('Injecting data into template...');
    let html = fs.readFileSync(path.join(__dirname, '../templates', 'dashboard.html'), 'utf-8');

    // Replace Metrics
    html = html.replace('{{totalMembers}}', metrics.totalMembers);
    html = html.replace('{{newMembers}}', metrics.newMembers);
    html = html.replace('{{activityRate}}', metrics.activityRate);

    // Replace Chart Images

    html = html.replace('{{typeChartImage}}', typeBase64);

    // Render Table Rows
    const rowsHtml = metrics.faculties.map(f => `
        <tr>
            <td>${f.nombre_ent_inst}</td>
            <td>${f.members}</td>
        </tr>
    `).join('');
    html = html.replace('{{facultiesRows}}', rowsHtml);

    // Debug
    fs.writeFileSync('debug_dashboard.html', html);

    console.log('Sending to JSReports...');
    try {
        const response = await axios.post(JSREPORT_URL, {
            template: {
                content: html,
                engine: 'none',
                recipe: 'chrome-pdf',
                chrome: {
                    // No need to wait for JS anymore, charts are images!
                }
            }
        }, {
            responseType: 'arraybuffer'
        });

        fs.writeFileSync(path.join(__dirname, '../../reports', 'community_report.pdf'), response.data);
        console.log('Success! Report saved to reports/community_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

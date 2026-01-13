const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getDenunciasData } = require('../db');
const { ChartJSNodeCanvas } = require('chartjs-node-canvas');

const JSREPORT_URL = 'http://localhost:5489/api/report';

const width = 400;
const height = 400;
const canvas = new ChartJSNodeCanvas({ width, height });

async function generateChart(data) {
    // Top users by complaints
    const config = {
        type: 'bar',
        data: {
            labels: data.map(d => d.usuario_reportado),
            datasets: [{
                label: 'Cantidad de Denuncias',
                data: data.map(d => parseInt(d.cantidad_denuncias)),
                backgroundColor: '#3498db',
                borderColor: '#2980b9',
                borderWidth: 1
            }]
        },
        options: {
            plugins: {
                legend: { display: false }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { precision: 0 }
                }
            }
        }
    };
    return await canvas.renderToBuffer(config);
}

async function generateReport() {
    console.log('Fetching denuncias data...');
    const data = await getDenunciasData();
    console.log(`Fetched ${data.length} records.`);

    // Prepare HTML rows
    const rowsHtml = data.map(d => `
        <tr>
            <td>${d.usuario_reportado}</td>
            <td>${d.cantidad_denuncias}</td>
            <td>${d.motivos || '-'}</td>
        </tr>
    `).join('');

    // Generate Chart
    console.log('Generating Chart...');
    let chartBase64 = '';
    try {
        const chartBuffer = await generateChart(data);
        chartBase64 = `data:image/png;base64,${chartBuffer.toString('base64')}`;
    } catch (err) {
        console.error("Chart generation failed:", err);
    }

    // Read Template
    let html = fs.readFileSync(path.join(__dirname, '../templates', 'denuncias.html'), 'utf-8');

    // Inject Data
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

        fs.writeFileSync(path.join(__dirname, '../pdfs', 'denuncias_report.pdf'), response.data);
        console.log('Success! Report saved to reports/pdfs/denuncias_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

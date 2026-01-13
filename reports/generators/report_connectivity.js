const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getConnectivityData } = require('../db');

const JSREPORT_URL = 'http://localhost:5489/api/report';

async function generateReport() {
    console.log('Fetching connectivity data...');
    const data = await getConnectivityData();
    console.log(`Fetched ${data.length} records.`);

    // Prepare HTML rows
    const rowsHtml = data.map(d => `
        <tr>
            <td>${d.usuario}</td>
            <td>${d.total_seguidores}</td>
            <td>${d.total_seguidos}</td>
            <td>${d.total_conexiones}</td>
        </tr>
    `).join('');

    // Read Template
    let html = fs.readFileSync(path.join(__dirname, '../templates', 'connectivity.html'), 'utf-8');

    // Inject Data
    html = html.replace('{{rows}}', rowsHtml);

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

        fs.writeFileSync(path.join(__dirname, '../pdfs', 'connectivity_report.pdf'), response.data);
        console.log('Success! Report saved to reports/pdfs/connectivity_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getGroupPerformanceData } = require('../db');

const JSREPORT_URL = 'http://localhost:5489/api/report';

async function generateReport() {
    console.log('Fetching group performance data...');
    const data = await getGroupPerformanceData();
    console.log(`Fetched ${data.length} records.`);

    // Prepare HTML rows
    const rowsHtml = data.map(g => `
        <tr>
            <td>${g.nombre_grupo}</td>
            <td>${g.tipo_grupo}</td>
            <td>${g.total_miembros}</td>
            <td>${g.fundadores}</td>
            <td>${g.moderadores}</td>
            <td>${g.miembros}</td>
        </tr>
    `).join('');

    // Read Template
    let html = fs.readFileSync(path.join(__dirname, '../templates', 'groups.html'), 'utf-8');

    // Inject Data
    html = html.replace('{{rows}}', rowsHtml);
    // Date replacement removed

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

        fs.writeFileSync(path.join(__dirname, '../../reports', 'group_performance_report.pdf'), response.data);
        console.log('Success! Report saved to reports/group_performance_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

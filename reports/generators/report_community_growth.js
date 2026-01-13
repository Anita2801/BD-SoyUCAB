const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getCommunityGrowthData } = require('../db');
const { ChartJSNodeCanvas } = require('chartjs-node-canvas');

const JSREPORT_URL = 'http://localhost:5489/api/report';

// Chart Setup
const width = 400; // Narrower for side column
const height = 300; // Aspect ratio optimized for side column
const chartCallback = (ChartJS) => {
    ChartJS.defaults.responsive = false;
    ChartJS.defaults.maintainAspectRatio = false;
    ChartJS.register({
        id: 'whiteBackground',
        beforeDraw: (chart) => {
            const ctx = chart.ctx;
            ctx.save();
            ctx.fillStyle = 'white';
            ctx.fillRect(0, 0, chart.width, chart.height);
            ctx.restore();
        }
    });
};
const chartJSNodeCanvas = new ChartJSNodeCanvas({ width, height, chartCallback });

async function generateReport() {
    try {
        console.log('Fetching data...');
        const data = await getCommunityGrowthData();
        console.log('Data fetched. Rows:', data ? data.length : 'null');


        // Calculate Summary Metrics
        const totalMembers = data.reduce((sum, item) => sum + parseInt(item.total_miembros), 0);
        const topCareer = data.length > 0 ? data[0].nombre_carrera : 'N/A';

        // Generate Chart
        console.log('Generating chart...');
        const chartConfig = {
            type: 'bar',
            data: {
                labels: data.map(d => d.codigo_carrera), // Use codes for cleaner x-axis
                datasets: [{
                    label: 'Miembros',
                    data: data.map(d => d.total_miembros),
                    backgroundColor: '#3498db',
                    borderColor: '#2980b9',
                    borderWidth: 1
                }]
            },
            options: {
                plugins: {
                    legend: { display: false },
                    title: { display: false } // HTML has title
                }
            }
        };
        const chartBuffer = await chartJSNodeCanvas.renderToBuffer(chartConfig);
        const chartBase64 = `data:image/png;base64,${chartBuffer.toString('base64')}`;

        // Prepare HTML
        console.log('preparing HTML...');
        let html = fs.readFileSync(path.join(__dirname, '../templates/community_growth.html'), 'utf-8');

        html = html.replace('{{totalMembers}}', totalMembers);
        html = html.replace('{{topCareer}}', topCareer);
        html = html.replace('{{chartImage}}', chartBase64);

        const tableRows = data.map(row => `
            <tr>
                <td>${row.codigo_carrera}</td>
                <td>${row.nombre_carrera}</td>
                <td>${row.total_miembros}</td>
            </tr>
        `).join('');
        html = html.replace('{{tableRows}}', tableRows);

        // Send to JSReport
        console.log('Rendering PDF with JSReport...');
        const response = await axios.post(JSREPORT_URL, {
            template: {
                content: html,
                engine: 'none',
                recipe: 'chrome-pdf'
            }
        }, {
            responseType: 'arraybuffer'
        });

        const outputPath = path.join(__dirname, '../pdfs', 'community_growth_report.pdf');
        fs.writeFileSync(outputPath, response.data);
        console.log(`Success! Report saved to: ${outputPath}`);

    } catch (error) {
        console.error('Error generating report:', error.message);
        console.error(error.stack);
        if (error.response) {
            console.error('JSReport Data:', error.response.data.toString());
        }
    }
}

generateReport();

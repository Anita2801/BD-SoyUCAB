const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getDiasporaData } = require('../db');
const { ChartJSNodeCanvas } = require('chartjs-node-canvas');
const Chart = require('chart.js');
const ChartGeo = require('chartjs-chart-geo');
const topojson = require('topojson-client');

const JSREPORT_URL = 'http://localhost:5489/api/report';

const width = 800;
const height = 500;
const chartCallback = (ChartJS) => {
    ChartJS.register(ChartGeo.BubbleMapController, ChartGeo.GeoFeature, ChartGeo.ProjectionScale, ChartGeo.SizeScale);
};
const canvas = new ChartJSNodeCanvas({ width, height, chartCallback });

async function generateMap(locations) {
    const worldAtlasPath = path.resolve(__dirname, '../../node_modules/world-atlas/countries-50m.json');
    if (!fs.existsSync(worldAtlasPath)) return null;

    const topology = JSON.parse(fs.readFileSync(worldAtlasPath, 'utf8'));
    const countries = topojson.feature(topology, topology.objects.countries).features;

    // Coordinate Mapping
    // Distrito Capital/Bolivar -> Venezuela (8.0, -66.0) approx center
    // Kinshasa -> DRC (-4.4, 15.2)
    // La Meca -> Saudi Arabia (24.0, 45.0)
    const bubbles = [];

    locations.forEach(l => {
        const val = parseInt(l.total_personas);
        if (l.ubicacion.includes('Distrito Capital') || l.ubicacion.includes('Bolívar')) {
            // Check if Venezuela already added? Just add a bubble. Or aggregate? 
            // Let's add separate bubbles for cities if we could, but for "Estado" let's just put them near center.
            // Dto Capital: 10.48, -66.90
            // Bolivar: 6.0, -63.0
            if (l.ubicacion.includes('Distrito')) bubbles.push({ latitude: 10.48, longitude: -66.90, value: val, label: 'Caracas' });
            else bubbles.push({ latitude: 6.0, longitude: -63.0, value: val, label: 'Bolívar' });
        }
        else if (l.ubicacion.includes('Kinshasa')) {
            bubbles.push({ latitude: -4.44, longitude: 15.26, value: val, label: 'Kinshasa' });
        }
        else if (l.ubicacion.includes('La Meca')) {
            bubbles.push({ latitude: 21.38, longitude: 39.85, value: val, label: 'Mecca' });
        }
    });

    const data = {
        labels: bubbles.map(b => b.label),
        datasets: [
            {
                // Background Map (Countries)
                outline: countries,
                showOutline: true,
                backgroundColor: '#154360', // Darker Blue World
                borderColor: '#5D6D7E', // Greyish border
                borderWidth: 0.5,
                data: countries.map(d => ({ feature: d, value: 0 }))
            },
            {
                // Bubbles
                label: 'Concentración',
                data: bubbles.map(b => ({
                    latitude: b.latitude,
                    longitude: b.longitude,
                    value: b.value
                })),
                backgroundColor: '#f1c40f', // Yellow Bubbles
                borderColor: '#f39c12',
                borderWidth: 1
            }
        ]
    };

    const config = {
        type: 'bubbleMap',
        data: data,
        options: {
            plugins: {
                legend: { display: false }
            },
            scales: {
                xy: {
                    projection: 'equalEarth'
                },
                r: {
                    type: 'size', // Explicitly define type to avoid RadialLinearScale fallback
                    size: [10, 30]
                }
            }
        }
    };

    return await canvas.renderToBuffer(config);
}

async function generateReport() {
    console.log('Fetching diaspora data...');
    const data = await getDiasporaData();
    console.log(`Fetched ${data.length} records.`);

    // Total Calculation
    const totalPeople = data.reduce((acc, curr) => acc + parseInt(curr.total_personas), 0);

    const rowsHtml = data.map(d => `
        <tr>
            <td>${d.ubicacion}</td>
            <td>${d.tipo_ubicacion}</td>
            <td>${d.total_personas}</td>
            <td>${d.roles_presentes ? d.roles_presentes.join(', ') : '-'}</td>
        </tr>
    `).join('');

    console.log('Generating Map...');
    let mapBase64 = '';
    try {
        const mapBuffer = await generateMap(data);
        if (mapBuffer) {
            mapBase64 = `data:image/png;base64,${mapBuffer.toString('base64')}`;
        }
    } catch (err) {
        console.error("Map generation failed:", err);
    }

    let html = fs.readFileSync(path.join(__dirname, '../templates', 'diaspora.html'), 'utf-8');

    html = html.replace('{{rows}}', rowsHtml);
    html = html.replace('{{mapImage}}', mapBase64);
    html = html.replace('{{totalPeople}}', totalPeople); // Inject Total

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

        fs.writeFileSync(path.join(__dirname, '../pdfs', 'diaspora_report.pdf'), response.data);
        console.log('Success! Report saved to reports/pdfs/diaspora_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

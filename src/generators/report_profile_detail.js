const fs = require('fs');
const path = require('path');
const axios = require('axios');
const { getProfile } = require('../db');

const JSREPORT_URL = 'http://localhost:5489/api/report';
const TEST_USER = 'mcervantes.02'; // Example user

async function generateReport() {
    console.log(`Fetching profile data for user: ${TEST_USER}...`);
    const profile = await getProfile(TEST_USER);

    if (!profile) {
        console.error('User not found!');
        return;
    }
    console.log('Profile found:', profile);

    // Read Template
    let html = fs.readFileSync(path.join(__dirname, '../templates', 'profile_detail.html'), 'utf-8');

    // Inject Data
    html = html.replace('{{nombre_usuario}}', profile.nombre_usuario || '-');
    html = html.replace('{{nombre_completo}}', profile.nombre_completo || '-');
    html = html.replace('{{tipo_perfil}}', profile.tipo_perfil || '-');
    html = html.replace('{{ubicacion}}', profile.ubicacion || '-');
    html = html.replace('{{descripcion}}', profile.descripcion || '-');
    html = html.replace('{{total_seguidores}}', profile.total_seguidores || '0');
    html = html.replace('{{total_seguidos}}', profile.total_seguidos || '0');

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

        fs.writeFileSync(path.join(__dirname, '../../reports', 'profile_detail_report.pdf'), response.data);
        console.log('Success! Report saved to reports/profile_detail_report.pdf');
    } catch (e) {
        console.error('Error generating report:', e.message);
        if (e.response) {
            console.error('JSReport response:', e.response.data.toString());
        }
    }
}

generateReport();

const { Pool } = require('pg');

const pool = new Pool({
    user: 'postgres',
    host: '127.0.0.1',
    database: 'bd_soyucab',
    password: 'password',
    port: 5433,
});

(async () => {
    try {
        console.log('Connecting to 127.0.0.1:5433...');
        const client = await pool.connect();
        console.log('Connected!');
        await client.release();
        await pool.end();
    } catch (e) {
        console.error('CONNECT ERROR:', e.message);
        console.error('CODE:', e.code);
    }
})();

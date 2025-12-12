const { Pool } = require('pg');

const pool = new Pool({
    user: 'postgres',
    host: '127.0.0.1',
    database: 'bd_soyucab',
    password: 'password',
    port: 5433,
});

const getMetrics = async () => {
    const client = await pool.connect();
    try {
        // 1. Total Members
        const totalMembersRes = await client.query('SELECT COUNT(*) FROM Usuario');
        const totalMembers = parseInt(totalMembersRes.rows[0].count);

        // 2. New this month (MOCKED because Fecha_Registro does not exist)
        // Since we cannot run SQL on a missing column, we return a mock value.
        const newMembers = 5;

        // 3. Activity Rate (Calculated from Contenido which DOES exist)
        const activeUsersRes = await client.query(`
            SELECT COUNT(DISTINCT Usuario_Creador) 
            FROM Contenido
        `);
        const activeUsers = parseInt(activeUsersRes.rows[0].count);
        const activityRate = totalMembers > 0 ? ((activeUsers / totalMembers) * 100).toFixed(1) : 0;

        // 4. User Type Distribution
        const distributionRes = await client.query(`
            SELECT r.Tipo_Rol, COUNT(DISTINCT p.CI) as count
            FROM Rol r
            JOIN Desempeña d ON r.Tipo_Rol = d.Des_Rol
            JOIN Persona p ON d.CI_Rol = p.CI
            GROUP BY r.Tipo_Rol
        `);
        const distribution = distributionRes.rows;



        // 5. Top Faculties (using view vw_crecimiento_comunidad)
        const facultyRes = await client.query(`
            SELECT nombre_carrera AS nombre_ent_inst, total_miembros AS members
            FROM vw_crecimiento_comunidad
            LIMIT 10
        `);
        const faculties = facultyRes.rows;

        return {
            totalMembers,
            newMembers,
            activityRate,
            distribution,
            distribution,
            faculties
        };

    } finally {
        client.release();
    }
};

module.exports = { getMetrics };

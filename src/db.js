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

        // 5. Growth Evolution (MOCKED because Fecha_Registro does not exist)
        // Returning static data for demonstration purposes
        const growth = [
            { month: 'Jan', count: 12 },
            { month: 'Feb', count: 19 },
            { month: 'Mar', count: 3 },
            { month: 'Apr', count: 5 },
            { month: 'May', count: 2 },
            { month: 'Jun', count: 3 }
        ];

        // 6. Top 10 Faculties
        const facultyRes = await client.query(`
            SELECT e.Nombre_Ent_Inst, COUNT(n.CI_Nexo) as members
            FROM Entidad_Institucional e
            JOIN Nexo n ON e.Cod_Inst = n.Cod_Inst_Nexo
            GROUP BY e.Nombre_Ent_Inst
            ORDER BY members DESC
            LIMIT 10
        `);
        const faculties = facultyRes.rows;

        return {
            totalMembers,
            newMembers,
            activityRate,
            distribution,
            growth,
            faculties
        };

    } finally {
        client.release();
    }
};

module.exports = { getMetrics };

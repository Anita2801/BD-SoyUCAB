$headers = @{ "Content-Type" = "application/json" }

# 0. Login to get Token
echo "Logging in as qdmancha.22..."
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method Post -Headers $headers -Body '{
    "cuenta": "qdmancha.22",
    "password": "12345"
}'
$token = $loginResponse.token
echo "Token obtained: $token"

$authHeaders = @{ 
    "Content-Type" = "application/json"
    "Authorization" = "Bearer $token"
}

# 1. Create Profile for Quijote
# Note: Assuming Usuario 'qdmancha.22' exists.
echo "Creating Profile for qdmancha.22..."
Invoke-RestMethod -Uri "http://localhost:8080/api/personas" -Method Post -Headers $authHeaders -Body '{
    "ci": "11223344",
    "usuario": { "cuenta": "qdmancha.22" },
    "primerNombre": "Quijote",
    "primerApellido": "Mancha",
    "sexo": "M",
    "lugar": { "nombre": "Caracas", "tipo": "Ciudad", "fk_lugar": null }
}'

# 2. Create Dummy Users (Usuario first, then Persona)
echo "Creating User: sofia.m..."
$sofiaAuth = Invoke-RestMethod -Uri "http://localhost:8080/auth/register" -Method Post -Headers $headers -Body '{
    "cuenta": "sofia.m",
    "email": "sofia@ucab.edu.ve",
    "password": "123"
}'
$sofiaToken = $sofiaAuth.token
$sofiaHeaders = @{ "Content-Type" = "application/json"; "Authorization" = "Bearer $sofiaToken" }

echo "Creating Profile for sofia.m..."
Invoke-RestMethod -Uri "http://localhost:8080/api/personas" -Method Post -Headers $sofiaHeaders -Body '{
    "ci": "55667788",
    "usuario": { "cuenta": "sofia.m" },
    "primerNombre": "Sofia",
    "primerApellido": "Martinez",
    "sexo": "F",
    "lugar": { "nombre": "Caracas", "tipo": "Ciudad", "fk_lugar": null }
}'

echo "Creating User: roberto.b..."
$robertoAuth = Invoke-RestMethod -Uri "http://localhost:8080/auth/register" -Method Post -Headers $headers -Body '{
    "cuenta": "roberto.b",
    "email": "roberto@ucab.edu.ve",
    "password": "123"
}'
$robertoToken = $robertoAuth.token
$robertoHeaders = @{ "Content-Type" = "application/json"; "Authorization" = "Bearer $robertoToken" }

echo "Creating Profile for roberto.b..."
Invoke-RestMethod -Uri "http://localhost:8080/api/personas" -Method Post -Headers $robertoHeaders -Body '{
    "ci": "99887766",
    "usuario": { "cuenta": "roberto.b" },
    "primerNombre": "Roberto",
    "primerApellido": "Blanco",
    "sexo": "M",
    "lugar": { "nombre": "Caracas", "tipo": "Ciudad", "fk_lugar": null }
}'

# 3. Create Events (Using Quijote's Token)
echo "Creating Event 1..."
$tomorrow = (Get-Date).AddDays(1).ToString("yyyy-MM-ddTHH:mm:ss")
$nextWeek = (Get-Date).AddDays(7).ToString("yyyy-MM-ddTHH:mm:ss")

Invoke-RestMethod -Uri "http://localhost:8080/api/eventos" -Method Post -Headers $authHeaders -Body "{
    `"usuarioOrganizador`": `"qdmancha.22`",
    `"fechaHora`": `"$tomorrow`",
    `"nombre`": `"Feria de Empleo UCAB`",
    `"descripcion`": `"Encuentro con empresas`",
    `"lugar`": `"Aula Magna`",
    `"categoria`": `"Academico`"
}"

echo "Creating Event 2..."
Invoke-RestMethod -Uri "http://localhost:8080/api/eventos" -Method Post -Headers $authHeaders -Body "{
    `"usuarioOrganizador`": `"sofia.m`",
    `"fechaHora`": `"$nextWeek`",
    `"nombre`": `"Conferencia IA`",
    `"descripcion`": `"El futuro es hoy`",
    `"lugar`": `"Auditorio`",
    `"categoria`": `"Tecnologia`"
}"

# 4. Create Groups (Using Quijote's Token)
echo "Creating Groups..."
Invoke-RestMethod -Uri "http://localhost:8080/api/grupos" -Method Post -Headers $authHeaders -Body '{
    "nombre": "Alumni UCAB Tech",
    "descripcion": "Egresados de Ingenieria",
    "numeroMiembros": 120
}'

Invoke-RestMethod -Uri "http://localhost:8080/api/grupos" -Method Post -Headers $authHeaders -Body '{
    "nombre": "Voluntariado UCAB",
    "descripcion": "Ayudando a la comunidad",
    "numeroMiembros": 850
}'

echo "Done seeding!"

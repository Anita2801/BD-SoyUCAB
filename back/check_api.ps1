$headers = @{ "Content-Type" = "application/json" }

echo "Logging in..."
$login = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method Post -Headers $headers -Body '{
    "cuenta": "qdmancha.22",
    "password": "12345"
}'
$token = $login.token
echo "Got Token: $token"

$authHeaders = @{ 
    "Content-Type" = "application/json"
    "Authorization" = "Bearer $token"
}

echo "Checking Events..."
try {
    $events = Invoke-RestMethod -Uri "http://localhost:8080/api/eventos/upcoming" -Method Get -Headers $authHeaders
    echo "Events: $($events | ConvertTo-Json -Depth 2)"
} catch {
    echo "Error fetching events: $_"
}

echo "Checking Groups..."
try {
    $groups = Invoke-RestMethod -Uri "http://localhost:8080/api/grupos/featured" -Method Get -Headers $authHeaders
    echo "Groups: $($groups | ConvertTo-Json -Depth 2)"
} catch {
    echo "Error fetching groups: $_"
}

echo "Checking Suggestions..."
try {
    $suggestions = Invoke-RestMethod -Uri "http://localhost:8080/api/personas/suggestions/qdmancha.22" -Method Get -Headers $authHeaders
    echo "Suggestions: $($suggestions | ConvertTo-Json -Depth 2)"
} catch {
    echo "Error fetching suggestions: $_"
}

# Start all services
$services = @(
    "service-discovery",
    "api-gateway",
    "customer-service",
    "product-service",
    "payment-service",
    "inventory-service",
    "order-service"
)

foreach ($service in $services) {
    Write-Host "Starting $service..."
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd $service; mvn spring-boot:run"
    # Wait for 30 seconds to ensure the service is up before starting the next one
    if ($service -eq "service-discovery") {
        Write-Host "Waiting for Eureka Server to start..."
        Start-Sleep -Seconds 30
    }
    elseif ($service -eq "api-gateway") {
        Write-Host "Waiting for API Gateway to start..."
        Start-Sleep -Seconds 20
    }
    else {
        Start-Sleep -Seconds 10
    }
}

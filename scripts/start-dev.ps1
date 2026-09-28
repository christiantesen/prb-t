$root = Split-Path -Parent $PSScriptRoot
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-11.0.32.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

Get-Content "$root\.env" | ForEach-Object {
  if ($_ -match '^\s*#' -or $_ -notmatch '=') { return }
  $name, $value = $_.Split('=', 2)
  Set-Item -Path "Env:$name" -Value $value
}

$mvn = Join-Path $root ".tools\apache-maven-3.9.9\bin\mvn.cmd"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\services\premieres'; npm start"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\services\candystore'; npm start"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\services\complete'; & '$mvn' spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\gateway'; & '$mvn' spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root\frontend'; npm run dev"
Write-Host "Servicios arrancando. Web en http://localhost:5173"

$secure = Read-Host "Enter salary_app PostgreSQL password" -AsSecureString
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try {
    $env:SALARY_DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    mvn spring-boot:run
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    Remove-Item Env:SALARY_DB_PASSWORD -ErrorAction SilentlyContinue
}

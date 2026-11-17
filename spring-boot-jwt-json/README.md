# Spring Boot – Direct JWT Generation from JSON Payload

A small REST API that accepts a JSON object and returns a signed JWT. It also provides a token validation endpoint.

## Requirements
- JDK 17+
- Maven 3.9+

## Run
From the project root:
```bash
mvn clean test
mvn spring-boot:run
```
Application base URL: `http://localhost:8080`

## 1. Generate a JWT directly from JSON
Request:
```bash
curl -X POST http://localhost:8080/api/jwt/generate ^
  -H "Content-Type: application/json" ^
  -d "{\"sub\":\"venu\",\"userId\":101,\"role\":\"ADMIN\",\"email\":\"venu@example.com\"}"
```
PowerShell users can use `curl.exe` or Postman. Example JSON body:
```json
{
  "sub": "venu",
  "userId": 101,
  "role": "ADMIN",
  "email": "venu@example.com"
}
```
Example response:
```json
{
  "tokenType": "Bearer",
  "token": "<signed-jwt>"
}
```
The service adds `iat` (issued-at) and `exp` (expiration) automatically. Default expiry is 60 minutes.

## 2. Validate a JWT
```bash
curl -X POST http://localhost:8080/api/jwt/validate   -H "Content-Type: application/json"   -d '{"token":"PASTE_TOKEN_HERE"}'
```

## Endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/jwt/generate` | Sign the supplied JSON claims |
| POST | `/api/jwt/validate` | Verify signature and expiration, then return claims |

## Configure a secure signing key
The included key is for local demonstration only. Generate a strong 256-bit (or longer) HMAC key and Base64-encode it. For example, with OpenSSL:
```bash
openssl rand -base64 32
```
Set the environment variable before starting the app:
```powershell
$env:JWT_SECRET="PASTE_GENERATED_BASE64_KEY"
$env:JWT_EXPIRATION_MINUTES="30"
mvn spring-boot:run
```
Never commit production signing keys to source control or share them with clients. A JWT is signed, not encrypted: its payload can be decoded by anyone holding the token. Do not put passwords, secrets, or sensitive personal data in claims. In a real authentication system, the server must derive claims from an authenticated identity rather than trusting arbitrary client-submitted roles or permissions.

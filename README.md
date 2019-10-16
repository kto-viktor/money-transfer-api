A little money transfer API exercise.

API reference:

### GET /accounts/{account_id}
Returns an account, or 404 code if account doesn't exist.  
Example response:
```
{
    "id": "00112233-4455-6677-8899-aabbccddeef1",
    "name": "my account",
    "balance": 0,
    "currency": "RUR"
}
```
Fields description:  
**id**: account id (UUID)    
**name**: account name (String)  
**balance**: account balance (Number)  
**currency**: account currency ISO code (String)  

### POST /accounts/
Create an account
Example request:
```
{
    "id": "00112233-4455-6677-8899-aabbccddeef1",
    "name": "my account",
    "currency": "RUR"
}
```
Returns a created account entity.  
Fields description see above. 

### POST /accounts/{account_id}/topup
Add balance to an account.
Example request:
```
{ 
   "requestId":"1bd44a1e-bf95-4901-bc79-8ce2f5ba6811",
   "systemId":"system_id",
   "amount":10
}
```
Fields description:  
**requestId**: idempotence key (String)    
**systemId**: initiator system id (String)    
**amount**: topup amount (Number)    
  
Returns a topup entity with status.
Example response:
```
{ 
   "requestId":"1bd44a1e-bf95-4901-bc79-8ce2f5ba6811",
   "systemId":"system_id",
   "amount":10,
   "status":0
}
```
Status codes:  
0: success  
1: in progress  
2: account not found

### POST /transfers/
Execute a money transfer.  
Example request:  
```
{
    "requestId": "f28b73d1-195c-42fa-ba57-bdace76e4e31",
    "systemId": "system_id",
    "sourceAccountId": "3662c3ea-029e-4a22-b9f7-7c536505c053",
    "targetAccountId": "48e72c64-f463-4905-b999-efc7f7af6439",
    "amount": 6
}
```
Fields description:  
**requestId**: idempotence key (String)    
**systemId**: initiator system id (String)  
**sourceAccountId**: source account id (UUID)      
**targetAccountId**: target account id (UUID)      
**amount**: transfer amount (Number)    

Returns a transfer with status structure.  
Example response:
```
{
    "requestId": "f28b73d1-195c-42fa-ba57-bdace76e4e31",
    "systemId": "system_id",
    "sourceAccountId": "3662c3ea-029e-4a22-b9f7-7c536505c053",
    "targetAccountId": "48e72c64-f463-4905-b999-efc7f7af6439",
    "amount": 6,
    "status": {
        "code": 4,
        "errorMessage": "Source account with id 3662c3ea-029e-4a22-b9f7-7c536505c053 not found."
    }
}
```
Status codes:  
0: success  
1: in progress  
2: insufficient funds  
3: currencies not match  
4: source account not found  
5: target account not found  

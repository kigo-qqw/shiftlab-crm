# CRM-система (Back-End) — Тестовое задание

## Сборка

```shell
git clone https://github.com/kigo-qqw/shiftlab-crm.git
cd shiftlab-crm
```

## Конфигурация
Настроить базу данных:
Создать базу данных, настроить application.properties или application.yml:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shiftlab-crm
spring.datasource.username=shiftlab-crm-user
spring.datasource.password=shiftlab-crm-password
```

## Запуск
```shell
./gradlew bootRun
```

## Тестирование
```shell
./gradlew test
```

Тестовое покрытие -- 77%


Документация реализована в Swagger (/swagger-ui/index.html)


# OpenAPI definition

# APIs

## GET /api/v1/seller

Get all sellers

Returns a list of all sellers




### Responses

#### 200


Successfully retrieved list


array







## PUT /api/v1/seller

Update seller (full)

Updates all seller fields




### Request Body

[SellerUpdateDto](#sellerupdatedto)







### Responses

#### 200


Seller updated successfully


[SellerDto](#sellerdto)







#### 400


Validation error


[ErrorDetailsDto](#errordetailsdto)






Examples




Validation error


```json
{
  "timestamp": "2025-08-27T23:01:34",
  "message": "field `name` must not be blank",
  "description": "uri=/api/v1/seller",
  "errorCode": 400
}
```



#### 404


Seller not found


[ErrorDetailsDto](#errordetailsdto)






Examples




Seller not found


```json
{
  "timestamp": "2025-08-27T12:34:56",
  "message": "There is no seller with id 42",
  "description": "uri=/api/v1/seller/42",
  "errorCode": 404
}
```



## POST /api/v1/seller

Create new seller

Creates a new seller and returns it




### Request Body

[SellerCreateDto](#sellercreatedto)







### Responses

#### 201


Seller created successfully


[SellerDto](#sellerdto)







#### 400


Validation error


[ErrorDetailsDto](#errordetailsdto)






Examples




Validation error


```json
{
  "timestamp": "2025-08-27T23:01:34",
  "message": "field `name` must not be blank",
  "description": "uri=/api/v1/seller",
  "errorCode": 400
}
```



## PATCH /api/v1/seller

Update seller (partial)

Updates only provided seller fields




### Request Body

[SellerPatchDto](#sellerpatchdto)







### Responses

#### 200


Seller updated successfully


[SellerDto](#sellerdto)







#### 400


Validation error


[ErrorDetailsDto](#errordetailsdto)






Examples




Validation error


```json
{
  "timestamp": "2025-08-27T23:01:34",
  "message": "field `name` must not be blank",
  "description": "uri=/api/v1/seller",
  "errorCode": 400
}
```



#### 404


Seller not found


[ErrorDetailsDto](#errordetailsdto)






Examples




Seller not found


```json
{
  "timestamp": "2025-08-27T12:34:56",
  "message": "There is no seller with id 42",
  "description": "uri=/api/v1/seller/42",
  "errorCode": 404
}
```



## GET /api/v1/transaction

Get all transactions

Returns a list of all transactions




### Responses

#### 200


Successfully retrieved list


array







## POST /api/v1/transaction

Create new transaction

Creates a new transaction and returns it




### Request Body

[TransactionCreateDto](#transactioncreatedto)







### Responses

#### 201


Transaction created successfully


[TransactionDto](#transactiondto)







#### 400


Validation error


[ErrorDetailsDto](#errordetailsdto)






Examples




Validation error


```json
{
  "timestamp": "2025-08-27T23:01:34",
  "message": "field `amount` must be greater than 0",
  "description": "uri=/api/v1/transaction",
  "errorCode": 400
}
```



## GET /api/v1/transaction/{id}

Get transaction by ID

Returns transaction details for the given ID


### Parameters

| Name | Type | Required | Description |
|------|------|----------|-------------|
| id | integer | True |  |


### Responses

#### 200


Successfully retrieved transaction


[TransactionDto](#transactiondto)







#### 404


Transaction not found


[ErrorDetailsDto](#errordetailsdto)






Examples




Transaction not found


```json
{
  "timestamp": "2025-08-27T12:34:56",
  "message": "There is no transaction with id 99",
  "description": "uri=/api/v1/transaction/99",
  "errorCode": 404
}
```



## GET /api/v1/seller/{id}

Get seller by ID

Returns seller details for the given ID


### Parameters

| Name | Type | Required | Description |
|------|------|----------|-------------|
| id | integer | True |  |


### Responses

#### 200


Successfully retrieved seller


[SellerDto](#sellerdto)







#### 404


Seller not found


[ErrorDetailsDto](#errordetailsdto)






Examples




Seller not found


```json
{
  "timestamp": "2025-08-27T12:34:56",
  "message": "There is no seller with id 42",
  "description": "uri=/api/v1/seller/42",
  "errorCode": 404
}
```



## DELETE /api/v1/seller/{id}

Delete seller

Deletes seller by ID


### Parameters

| Name | Type | Required | Description |
|------|------|----------|-------------|
| id | integer | True |  |


### Responses

#### 204


Seller deleted successfully




#### 404


Seller not found


[ErrorDetailsDto](#errordetailsdto)






Examples




Seller not found


```json
{
  "timestamp": "2025-08-27T12:34:56",
  "message": "There is no seller with id 42",
  "description": "uri=/api/v1/seller/42",
  "errorCode": 404
}
```



## GET /api/v1/seller/transaction/{id}

Get transactions by seller ID

Returns all transactions for the given seller ID


### Parameters

| Name | Type | Required | Description |
|------|------|----------|-------------|
| id | integer | True |  |


### Responses

#### 200


Successfully retrieved seller transactions


array







#### 404


Seller not found


[ErrorDetailsDto](#errordetailsdto)






Examples




Seller not found


```json
{
  "timestamp": "2025-08-27T12:34:56",
  "message": "There is no seller with id 42",
  "description": "uri=/api/v1/seller/42/transaction",
  "errorCode": 404
}
```



## GET /api/v1/seller/top-seller

Get top seller by income

Returns the seller with the highest total income for a given period


### Parameters

| Name | Type | Required | Description |
|------|------|----------|-------------|
| start | string | True | Start of the period |
| end | string | True | End of the period |


### Responses

#### 200


Successfully retrieved top seller


[SellerWithIncomeDto](#sellerwithincomedto)







#### 400


Invalid request parameters


[ErrorDetailsDto](#errordetailsdto)






Examples




Invalid date range


```json
{
  "timestamp": "2025-08-29T12:00:00",
  "message": "End date must be after start date",
  "description": "uri=/api/v1/seller/top-seller",
  "errorCode": 400
}
```



## GET /api/v1/seller/sellers-with-income-less-threshold

Get sellers with income below threshold

Returns all sellers whose total income for the given period is below the specified threshold


### Parameters

| Name | Type | Required | Description |
|------|------|----------|-------------|
| start | string | True | Start of the period |
| end | string | True | End of the period |
| threshold | number | True | Income threshold |


### Responses

#### 200


Successfully retrieved sellers


array







#### 400


Invalid request parameters


[ErrorDetailsDto](#errordetailsdto)






Examples




Invalid threshold


```json
{
  "timestamp": "2025-08-29T12:00:00",
  "message": "Threshold must be greater than zero",
  "description": "uri=/api/v1/seller/sellers-with-income-less-threshold",
  "errorCode": 400
}
```



# Components



## SellerUpdateDto



| Field | Type | Description |
|-------|------|-------------|
| id | integer | Unique seller identifier |
| name | string | Seller name |
| contactInfo | string | Contact information |


## SellerDto



| Field | Type | Description |
|-------|------|-------------|
| id | integer | Unique seller identifier |
| name | string | Seller name |
| contactInfo | string | Contact information |
| registrationDate | string | Seller registration date |


## ErrorDetailsDto



| Field | Type | Description |
|-------|------|-------------|
| timestamp | string | Time when the error occurred |
| message | string | Human-readable error message |
| description | string | Detailed description of the error |
| errorCode | integer | HTTP status code of the error (duplicates the response status) |


## TransactionCreateDto



| Field | Type | Description |
|-------|------|-------------|
| sellerId | integer | Seller identifier |
| amount | number | Transaction amount |
| paymentType | string | Payment type |


## TransactionDto



| Field | Type | Description |
|-------|------|-------------|
| id | integer | Transaction identifier |
| seller |  | Seller details associated with the transaction |
| amount | number | Transaction amount |
| paymentType | string | Payment type |
| transactionDate | string | Transaction date (UTC) |


## SellerCreateDto



| Field | Type | Description |
|-------|------|-------------|
| name | string | Seller name |
| contactInfo | string | Contact information |


## SellerPatchDto



| Field | Type | Description |
|-------|------|-------------|
| id | integer | Unique seller identifier |
| name | string | Seller name |
| contactInfo | string | Contact information |


## SellerWithIncomeDto



| Field | Type | Description |
|-------|------|-------------|
| seller |  | Seller details |
| sumOfTransactionAmount | number | Total income from all transactions |

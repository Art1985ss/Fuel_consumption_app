# Fuel_consumption_app
Task project
### Endpoints

post api/v1/records - saves record

get api/v1/records - returns all records

get api/v1/records/money_spent - total spent amount of money grouped by month

get api/v1/records/{month} - list fuel consumption records for specified month (each row should contain: fuel type, volume, date, price, total price, driver ID)

get api/v1/records/statistics - statistics for each month, list fuel consumption records grouped by fuel type (each row should contain: fuel type, volume, average price, total price)

post api/v1/records/upload - upload records from file

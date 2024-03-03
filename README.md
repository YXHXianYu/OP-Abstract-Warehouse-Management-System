# OP Abstract Warehouse Management System

## How to deploy

### Using Docker

* Deploy
  ```
  git clone git@github.com:YXHXianYu/OP-Abstract-Warehouse-Management-System.git
  cd OP-Abstract-Warehouse-Management-System
  docker-compose up
  ```

* Rebuild without cache
  ```
  docker-compose build --no-cache
  docker-compose up
  ```

### Project Configuration

* Frontend
  * Vue.js + Vite + Node v18
  * Port: 5173

* Backend
  * Sprintboot + Java17
  * Port: 8080

* Database
  * MySQL
  * Port: 3306
  * Database name: op
  * Initialization script: `./sql/init.sql`


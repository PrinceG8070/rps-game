Step 1: 

Action: Double-click 
start-mysql-admin.bat
Or 

cmd

net start MySQL80

Step 2:

Action: Double-click  start-backend.bat
.
Or via Terminal:

bash
cd backend
gradlew.bat bootRun

http://localhost:8080/api.



Step 3: 

bash
cd frontend
npm install


Step 4: 

bash
cd frontend
npm run dev

http://localhost:5173/



SELECT * FROM users;

SELECT * FROM game_scores;

SELECT * FROM game_rounds;

SELECT * FROM login_logs;

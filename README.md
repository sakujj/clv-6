<h2>Tasks servlets</h2>
<p>Для запуска всего приложения без тестов: <code>docker compose up</code></p>
<p>Для запуска юнит тестов: запустить тесты в <code>src/test/java/unit</code></p>
<p>Для запуска юнит и интеграционных тестов: 
    <ol>
    <li><code>cd ./database</code></li>
    <li><code>docker compose up</code> (Поднимаем тестовую БД)</li>
    <li><code>cd ..</code></li>
    <li><code>./gradlew test</code></li>
    </ol>
</p>
<p>Адрес приложения: http://localhost:80/task-servlets</p>
<p>Адрес REST API: http://localhost:80/task-servlets/api/1/clients</p>
<p>Адрес PDF: http://localhost:80/task-servlets/pdf/clients</p>
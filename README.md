<h2>Task spring framework context</h2>
Внедрен спринг контекст.
Оставлены сервлеты и DAO; для того чтобы использовать в них спринг контекст, создан синглтон класс
AppContext, являющийся оберткой над AnnotationConfigApplicationContext. Б<u>о</u>льшая часть бинов 
созданы внутри класса <code>by.sakujj.config.SpringConfig</code>.
В интеграционных тестах, чтобы не поднимать тестовую базу в Docker вручную, применены Test Containers.</p>
<p>Для запуска всего приложения без тестов: <code>docker compose up</code></p>
<p>Для запуска юнит тестов: запустить тесты в <code>src/test/java/unit</code></p>
<p>Для запуска интеграционных тестов: 
    <ol>
    <li>запустить Docker;</li>
    <li>запустить тесты в <code>src/test/java/integration</code>.</li>
    </ol>
</p>
<p>Адрес приложения: http://localhost:80/task-servlets</p>
<p>Адрес REST API: http://localhost:80/task-servlets/api/1/clients</p>
<p>Адрес PDF: http://localhost:80/task-servlets/pdf/clients</p>
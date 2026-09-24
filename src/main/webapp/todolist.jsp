<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Список задач</title>
    <link rel="stylesheet" href="${contextPath}/reset.css">
    <link rel="stylesheet" href="${contextPath}/style.css">
</head>
<body>
<div class="main-block">
    <h1 class="main-header">Список задач</h1>

    <form action="${contextPath}/" method="POST" class="new-todo-form">
        <div>
            <label for="new-todo-item-text-field">Введите текст задачи</label>
        </div>

        <div class="field-wrapper${not empty createError ? ' invalid' : ''}">
            <input id="new-todo-item-text-field" type="text" name="text">
            <div class="error-message">${createError}</div>
        </div>

        <button type="submit" name="action" value="create">Создать</button>
    </form>

    <div class="general-error">${generalError}</div>

    <ul class="todo-list">
        <c:forEach var="item" items="${todoItems}">
            <li>
                <c:choose>
                    <c:when test="${editId == item.id}">
                        <form class="edit-form" action="${contextPath}/" method="POST">
                            <div class="edit-container${not empty saveError ? ' invalid' : ''}">
                                <input class="edit-todo-item-text-field" type="text" name="text"
                                       value="${editText != null ? editText : item.text}">
                                <div class="error-message">${saveError}</div>
                            </div>

                            <button class="save-button" type="submit" name="action" value="save">Сохранить</button>
                            <button class="cancel-button" type="submit" name="action" value="cancel">Отменить</button>

                            <input type="hidden" name="id" value="${item.id}">
                        </form>
                    </c:when>

                    <c:otherwise>
                        <span class="todo-item-text">${item.text}</span>

                        <form action="${contextPath}/" method="POST">
                            <button class="edit-button" type="submit" name="action" value="edit">Редактировать</button>
                            <button class="delete-button" type="submit" name="action" value="delete">Удалить</button>

                            <input type="hidden" name="id" value="${item.id}">
                        </form>
                    </c:otherwise>
                </c:choose>
            </li>
        </c:forEach>
    </ul>
</div>
</body>
</html>
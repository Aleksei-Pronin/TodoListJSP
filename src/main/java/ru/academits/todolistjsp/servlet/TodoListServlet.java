package ru.academits.todolistjsp.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.academits.todolistjsp.data.TodoItem;
import ru.academits.todolistjsp.data.TodoItemsInMemoryRepository;
import ru.academits.todolistjsp.data.TodoItemsRepository;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet("")
public class TodoListServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1231L;

    private TodoItemsRepository todoItemsRepository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        todoItemsRepository = new TodoItemsInMemoryRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();

        String createError = session.getAttribute("createError") != null
                ? session.getAttribute("createError").toString()
                : "";

        String saveError = session.getAttribute("saveError") != null
                ? session.getAttribute("saveError").toString()
                : "";

        String editText = session.getAttribute("editText") != null
                ? session.getAttribute("editText").toString()
                : null;

        Integer editId = (Integer) session.getAttribute("editId");

        req.setAttribute("createError", createError);
        req.setAttribute("saveError", saveError);
        req.setAttribute("editText", editText);
        req.setAttribute("editId", editId);

        session.removeAttribute("createError");
        session.removeAttribute("saveError");

        List<TodoItem> todoItems = todoItemsRepository.getAll();
        req.setAttribute("todoItems", todoItems);

        req.getRequestDispatcher("/todolist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        try {
            switch (action) {
                case "create" -> {
                    String text = req.getParameter("text");

                    if (text == null || text.isBlank()) {
                        HttpSession session = req.getSession();
                        session.setAttribute("createError", "Необходимо заполнить поле");
                    } else {
                        todoItemsRepository.create(text.trim());
                    }
                }

                case "edit" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    req.getSession().setAttribute("editId", id);
                }

                case "save" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    String text = req.getParameter("text");

                    if (text == null || text.isBlank()) {
                        HttpSession session = req.getSession();
                        session.setAttribute("editId", id);
                        session.setAttribute("editText", text);
                        session.setAttribute("saveError", "Необходимо заполнить поле");
                    } else {
                        todoItemsRepository.update(new TodoItem(id, text.trim()));
                        req.getSession().removeAttribute("editId");
                        req.getSession().removeAttribute("editText");
                    }
                }

                case "cancel" -> {
                    req.getSession().removeAttribute("editId");
                    req.getSession().removeAttribute("editText");
                }

                case "delete" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    todoItemsRepository.delete(id);
                    req.getSession().removeAttribute("editId");
                }
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        resp.sendRedirect(getServletContext().getContextPath() + "/");
    }
}
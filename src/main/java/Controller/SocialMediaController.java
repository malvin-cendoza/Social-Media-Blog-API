package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;
import java.util.Optional;

/**
 * Main Controller for the Social Media Blog API.
 * Sets up all HTTP endpoints and routes requests to service layer.
 */
public class SocialMediaController {
    private AccountService accountService = new AccountService();
    private MessageService messageService = new MessageService();
    /**
     * Initializes the Javalin app and defines all REST API endpoints.
     * @return Configured Javalin app.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        
        // Auth endpoints
        app.post("/register", this::handleRegister);
        app.post("/login", this::handleLogin);

        // Message endpoints
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{message_id}", this::getMessageById);
        app.delete("/messages/{message_id}", this::deleteMessageById);
        app.patch("/messages/{message_id}", this::updateMessage);

        // Messages by user
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountId);

        return app;
    }

    /**
     * Registers a new user.
     * Fails with 400 if username is blank, password is < 4 chars, or username already exists.
     */
    private void handleRegister(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        Account created = accountService.register(account);
        if (created != null) {
            ctx.json(created);
        } else {
            ctx.status(400);
        }
    }

    /**
     * Logs in an existing user.
     * Fails with 401 if credentials are invalid.
     */
    private void handleLogin(Context ctx) {
        Account login = ctx.bodyAsClass(Account.class);
        Account result = accountService.login(login);
        if (result != null) {
            ctx.json(result);
        } else {
            ctx.status(401);
        }
    }

    /**
     * Creates a new message.
     * Fails with 400 if message_text is blank, too long, or posted_by is invalid.
     */
    private void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        Message created = messageService.createMessage(message);
        if (created != null) {
            ctx.json(created);
        } else {
            ctx.status(400);
        }
    }

    /**
     * Returns all messages in the system.
     */
    private void getAllMessages(Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    /**
     * Returns a specific message by ID.
     * Returns empty body if not found.
     */
    private void getMessageById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        Optional<Message> message = messageService.getMessageById(id);
        if (message.isPresent()) {
            ctx.json(message.get());
        } else {
            ctx.result("");
        }
    }

    /**
     * Deletes a message by ID.
     * Returns the deleted message or an empty body if message didn't exist (idempotent).
     */
    private void deleteMessageById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        Optional<Message> message = messageService.deleteMessageById(id);
        if (message.isPresent()) {
            ctx.json(message.get());
        } else {
            ctx.result("");
        }        
    }

    /**
     * Updates message_text of a message by ID.
     * Fails with 400 if message doesn't exist or text is invalid.
     */
    private void updateMessage(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        Message input = ctx.bodyAsClass(Message.class);
        Optional<Message> updated = messageService.updateMessage(id, input.getMessage_text());
        if (updated.isPresent()) {
            ctx.json(updated.get());
        } else {
            ctx.status(400);
        }
    }

    /**
     * Gets all messages posted by a specific user account.
     */
    private void getMessagesByAccountId(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        ctx.json(messages);
    }
}
package Controller;

import java.util.List;

import static org.mockito.ArgumentMatchers.matches;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::postRegisterHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByMessageIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByMessageId);
        app.patch("/messages/{message_id}", this::patchMessagebyMessageId);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesByAccountIdHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    private void postRegisterHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        if (account.getUsername().length() <= 0 || account.getPassword().length() <= 3) {
            ctx.status(400);
            return;
        }

        if (accountService.getAccountByUsername(account.getUsername()) != null) {
            ctx.status(400);
            return;
        }

        Account addedAccount = accountService.addAccount(account);
        System.out.println("Account being added...");
        ctx.json(addedAccount);
        ctx.status(200);
    }

    private void postLoginHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        Account existingAccount = accountService.getAccountByUsername(account.getUsername());

        if (existingAccount == null) {
            ctx.status(401);
            return;
        }

        if (!existingAccount.getPassword().equals(account.getPassword())) {
            ctx.status(401);
            return;
        }

        ctx.json(existingAccount);
        ctx.status(200);
    }

    private void postMessageHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);

        if (message.getMessage_text().length() == 0 || message.getMessage_text().length() >= 256) {
            ctx.status(400);
            return;
        }

        if (!accountService.accountExists(message.getPosted_by())) {
            ctx.status(400);
            return;
        }

        Message addedMessage = messageService.addMessage(message);
        ctx.json(addedMessage);
        ctx.status(200);
    }

    private void getAllMessagesHandler(Context ctx) throws JsonProcessingException{
        List<Message> allMessages = messageService.getAllMessages();
        ctx.json(allMessages);
        ctx.status(200);
    }

    private void getMessageByMessageIdHandler(Context ctx) throws JsonProcessingException{
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageByMessageId(messageId);
        if (message == null) {
            ctx.status(200);
            ctx.result("");
        } else {
            ctx.json(message);
            ctx.status(200);
        }
    }

    private void deleteMessageByMessageId(Context ctx) throws JsonProcessingException{
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageByMessageId(messageId);
        if (message == null) {
            ctx.status(200);
            ctx.result("");
        } else {
            messageService.deleteMessageByMessageId(messageId);
            ctx.json(message);
            ctx.status(200);
        }
    }

    private void patchMessagebyMessageId(Context ctx) throws JsonProcessingException{
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message existingMessage = messageService.getMessageByMessageId(messageId);

        if (existingMessage == null) {
            ctx.status(400);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        Message bodyMessage = mapper.readValue(ctx.body(), Message.class);
        String newText = bodyMessage.getMessage_text();

        if (newText == null || newText.trim().isEmpty() || newText.length() > 255) {
            ctx.status(400);
            return;
        }

        Message updatedMessage = messageService.updateMessageByMessageId(messageId, newText);

        if (updatedMessage != null) {
            ctx.json(updatedMessage);
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }

    private void getAllMessagesByAccountIdHandler(Context ctx) throws JsonProcessingException{
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getAllMessagesByAccountId(accountId);
        ctx.json(messages);
        ctx.status(200);
    }
}
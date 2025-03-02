package yuuria.stackupper.configlibrary.ast;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import yuuria.stackupper.configlibrary.ConfigLibrary;
import yuuria.stackupper.configlibrary.Constant;
import yuuria.stackupper.configlibrary.Property;
import yuuria.stackupper.language.StackUpperBaseListener;
import yuuria.stackupper.language.StackUpperParser;

public class Listener extends StackUpperBaseListener {
    @Override
    public void exitStart(StackUpperParser.StartContext ctx)
    {
        if (ctx.getStop().getType() == Token.EOF) {
            if (Constant.UnprocessedCollection.isEmpty()) {
                ConfigLibrary.logger.error("UnprocessedCollection is empty");
                return;
            }
            ConfigLibrary.logger.info("Listener EOF UnprocessedCollection is not empty");
            for (Property.RegexProperty regexProperty : Constant.UnprocessedCollection) {
                Property.processRegexProperty(regexProperty);
            }
            Constant.UnprocessedCollection.clear();
        }
    }

    @Override
    public void exitIdStatement(StackUpperParser.IdStatementContext ctx)
    {
        StackUpperParser.ProgramStatementsContext parent = (StackUpperParser.ProgramStatementsContext) ctx.getParent();
        TerminalNode _id = ctx.getToken(StackUpperParser.STRING, 0);
        String id = _id.getText().replaceAll("['\"]", "");
        AssignOperator assignOperator = AssignOperator.from(parent.assignOp().getText());
        long opNumber = Long.parseLong(parent.NUMBER().getText());

        if (ctx.TILDE() != null) {
            id = id.replaceAll("\\*", ".*");
            Constant.UnprocessedCollection.add(new Property.RegexProperty(id, assignOperator, opNumber));
            return;
        }

        ResourceLocation tryParseId = ResourceLocation.tryParse(id);
        ConfigLibrary.logger.info("id statement with {}, resourceLocation tryparse out: {}", id, tryParseId);
        if (tryParseId == null && ctx.TILDE() == null)
        {
            return;
        }
        tryParseId = ResourceLocation.parse(id);
        Item itemItem = Constant.getItemFromBuiltinRegistries(tryParseId);
        ItemStack itemItemStack = new ItemStack(itemItem);
        Constant.ItemCollection.put(
                itemItem,
                new Property(
                        tryParseId.getNamespace(),
                        tryParseId.getPath(),
                        assignOperator,
                        opNumber,
                        itemItemStack
                )
        );
    }

    @Override
    public void exitTagStatement(StackUpperParser.TagStatementContext ctx) {
        StackUpperParser.ProgramStatementsContext parent = (StackUpperParser.ProgramStatementsContext) ctx.getParent();
        String tagInput = ctx.STRING().getText().replaceAll("[\"'#]", "");
        AssignOperator assignOperator = AssignOperator.from(parent.assignOp().getText());
        Number opNumber = Long.parseLong(parent.NUMBER().getText());

        if (ctx.TILDE() != null)
        {
            tagInput = tagInput.replaceAll("\\*", ".*");
            Constant.UnprocessedCollection.add(new Property.RegexProperty(tagInput, assignOperator, opNumber, true, true));
            return;
        }

        if (ResourceLocation.tryParse(tagInput) == null)
        {
            return;
        }

        Constant.UnprocessedCollection.add(new Property.RegexProperty(tagInput, assignOperator, opNumber, true));
    }

    @Override
    public void exitSizeStatement(StackUpperParser.SizeStatementContext ctx) {
        StackUpperParser.ProgramStatementsContext parent = (StackUpperParser.ProgramStatementsContext) ctx.getParent();
        AssignOperator assignOperator = AssignOperator.from(parent.assignOp().getText());
        CompareOperator compareOperator = CompareOperator.from(ctx.compareOp().getText());
        Number compareBy = Long.parseLong(ctx.NUMBER().getText());
        Number opNumber = Long.parseLong(parent.NUMBER().getText());

        Constant.UnprocessedCollection.add(new Property.RegexProperty(assignOperator, compareOperator, opNumber, compareBy));
    }
}

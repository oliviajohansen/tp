package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INGREDIENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Arrays;
import java.util.stream.Stream;

import seedu.address.logic.commands.SearchRecipeCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.recipe.NameContainsKeywordsPredicate;
import seedu.address.model.recipe.RecipeContainsIngredientsPredicate;
import seedu.address.model.recipe.RecipeContainsKeywordsPredicate;
import seedu.address.model.recipe.TagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class SearchRecipeCommandParser implements Parser<SearchRecipeCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SearchRecipeCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_TAG, PREFIX_INGREDIENT);

        if ((!arePrefixesPresent(argMultimap, PREFIX_NAME) && !arePrefixesPresent(argMultimap, PREFIX_TAG)
                && !arePrefixesPresent(argMultimap, PREFIX_INGREDIENT))
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchRecipeCommand.MESSAGE_USAGE));
        }

        String name = argMultimap.getValue(PREFIX_NAME).isPresent() ? argMultimap.getValue(PREFIX_NAME).get() : "";
        String tag = argMultimap.getValue(PREFIX_TAG).isPresent() ? argMultimap.getValue(PREFIX_TAG).get() : "";
        String ingredient = argMultimap.getValue(PREFIX_INGREDIENT).isPresent()
                ? argMultimap.getValue(PREFIX_INGREDIENT).get() : "";

        String trimmedName = name.trim();
        String trimmedTag = tag.trim();
        String trimmedIngredient = ingredient.trim();

        if (trimmedName.isEmpty() && trimmedTag.isEmpty() && trimmedIngredient.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchRecipeCommand.MESSAGE_USAGE));
        }

        RecipeContainsKeywordsPredicate pred = parsePredicates(trimmedName, trimmedTag, trimmedIngredient);

        return new SearchRecipeCommand(pred);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    private RecipeContainsKeywordsPredicate parsePredicates(String trimmedName, String trimmedTag,
                                                            String trimmedIngredient)
            throws ParseException {

        if (!trimmedName.isEmpty()) {
            String[] nameKeywords = trimmedName.split("\\s+");
            return new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
        } else if (!trimmedTag.isEmpty()) {
            String[] tagKeywords = trimmedTag.split("\\s+");
            return new TagContainsKeywordsPredicate(Arrays.asList(tagKeywords));
        } else if (!trimmedIngredient.isEmpty()) {
            String[] ingredientKeywords = trimmedIngredient.split("\\s+");
            return new RecipeContainsIngredientsPredicate(Arrays.asList(ingredientKeywords));
        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchRecipeCommand.MESSAGE_USAGE));
        }
    }

}

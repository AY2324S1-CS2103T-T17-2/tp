package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.SortOrder;
import seedu.address.model.Model;

/**
 * Sorts and lists all companies in the address book to the user by their deadlines.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_SUCCESS_ASCENDING = "Sorted all companies by their deadlines in ascending order";
    public static final String MESSAGE_SUCCESS_DESCENDING = "Sorted all companies by their deadlines in descending "
            + "order";

    public static final String MESSAGE_USAGE =
            "Format: " + COMMAND_WORD + " SORT_ORDER. "
            + "Valid ascending sort orders: a, asc, ascending. Valid descending sort orders:  d, desc, descending, \n"
            + "Example: " + COMMAND_WORD + " ascending";

    private final SortOrder sortOrder;

    /**
     * Creates a SortCommand to sort the companies by their deadlines.
     *
     * @param sortOrder the order in which the companies are sorted.
     */
    public SortCommand(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.sortCompaniesByDeadline(sortOrder);
        return new CommandResult(sortOrder == SortOrder.ASCENDING ? MESSAGE_SUCCESS_ASCENDING
                : MESSAGE_SUCCESS_DESCENDING);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SortCommand)) {
            return false;
        }

        SortCommand otherSortCommand = (SortCommand) other;
        return sortOrder == otherSortCommand.sortOrder;
    }

    @Override
    public String toString() {
        return COMMAND_WORD + " " + sortOrder.toString();
    }
}

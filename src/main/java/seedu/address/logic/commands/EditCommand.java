package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RECRUITER_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_COMPANIES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.ApplicationStatus;
import seedu.address.model.company.Company;
import seedu.address.model.company.Deadline;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.company.RecruiterName;
import seedu.address.model.company.Role;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing company in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    //c/COMPANY_NAME n/RECRUITER_NAME r/ROLE a/APPLICATION_STATUS d/DEADLINE [e/EMAIL] [p/PHONE_NUMBER]

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the company identified "
            + "by the index number used in the displayed company list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_COMPANY_NAME + "COMPANY_NAME] "
            + "[" + PREFIX_RECRUITER_NAME + "RECRUITER_NAME] "
            + "[" + PREFIX_ROLE + "ROLE] "
            + "[" + PREFIX_STATUS + "APPLICATION_STATUS] "
            + "[" + PREFIX_DEADLINE + "DEADLINE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_COMPANY_SUCCESS = "%1$s company edited.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_COMPANY = "This company already exists in the address book.";

    private final Index index;
    private final EditCompanyDescriptor editCompanyDescriptor;

    /**
     * @param index of the company in the filtered company list to edit
     * @param editCompanyDescriptor details to edit the company with
     */
    public EditCommand(Index index, EditCompanyDescriptor editCompanyDescriptor) {
        requireNonNull(index);
        requireNonNull(editCompanyDescriptor);

        this.index = index;
        this.editCompanyDescriptor = new EditCompanyDescriptor(editCompanyDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Company> lastShownList = model.getFilteredCompanyList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
        }

        Company companyToEdit = lastShownList.get(index.getZeroBased());
        Company editedCompany = createEditedCompany(companyToEdit, editCompanyDescriptor);

        if (!companyToEdit.isSameCompany(editedCompany) && model.hasCompany(editedCompany)) {
            throw new CommandException(MESSAGE_DUPLICATE_COMPANY);
        }

        model.setCompany(companyToEdit, editedCompany);
        model.updateFilteredCompanyList(PREDICATE_SHOW_ALL_COMPANIES);
        model.setCurrentViewedCompany(editedCompany);
        return new CommandResult(String.format(MESSAGE_EDIT_COMPANY_SUCCESS, Messages.getCompanyName(editedCompany)));
    }

    /**
     * Creates and returns a {@code Company} with the details of {@code companyToEdit}
     * edited with {@code editCompanyDescriptor}.
     */
    private static Company createEditedCompany(Company companyToEdit, EditCompanyDescriptor editCompanyDescriptor) {
        assert companyToEdit != null;

        Name updatedName = editCompanyDescriptor.getName().orElse(companyToEdit.getName());
        Phone updatedPhone = editCompanyDescriptor.getPhone().orElse(companyToEdit.getPhone());
        Email updatedEmail = editCompanyDescriptor.getEmail().orElse(companyToEdit.getEmail());
        Role updatedRole = editCompanyDescriptor.getRole().orElse(companyToEdit.getRole());
        Deadline updatedDeadline = editCompanyDescriptor.getDeadline().orElse(companyToEdit.getDeadline());
        ApplicationStatus updatedStatus = editCompanyDescriptor.getStatus().orElse(companyToEdit.getStatus());
        RecruiterName updatedRecruiterName = editCompanyDescriptor.getRecruiterName()
                .orElse(companyToEdit.getRecruiterName());
        Set<Tag> updatedTags = editCompanyDescriptor.getTags().orElse(companyToEdit.getTags());

        return new Company(updatedName, updatedPhone, updatedEmail, updatedRole, updatedDeadline,
                updatedStatus, updatedRecruiterName, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editCompanyDescriptor.equals(otherEditCommand.editCompanyDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editCompanyDescriptor", editCompanyDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the company with. Each non-empty field value will replace the
     * corresponding field value of the company.
     */
    public static class EditCompanyDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Role role;
        private Deadline deadline;
        private ApplicationStatus status;
        private RecruiterName recruiterName;
        private Set<Tag> tags;

        public EditCompanyDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditCompanyDescriptor(EditCompanyDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setRole(toCopy.role);
            setDeadline(toCopy.deadline);
            setStatus(toCopy.status);
            setRecruiterName(toCopy.recruiterName);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, role, deadline, status, recruiterName, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Optional<Role> getRole() {
            return Optional.ofNullable(role);
        }

        public void setDeadline(Deadline deadline) {
            this.deadline = deadline;
        }

        public Optional<Deadline> getDeadline() {
            return Optional.ofNullable(deadline);
        }

        public void setStatus(ApplicationStatus status) {
            this.status = status;
        }

        public Optional<ApplicationStatus> getStatus() {
            return Optional.ofNullable(status);
        }

        public void setRecruiterName(RecruiterName recruiterName) {
            this.recruiterName = recruiterName;
        }

        public Optional<RecruiterName> getRecruiterName() {
            return Optional.ofNullable(recruiterName);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditCompanyDescriptor)) {
                return false;
            }

            EditCompanyDescriptor otherCompanyDescriptor = (EditCompanyDescriptor) other;
            return Objects.equals(name, otherCompanyDescriptor.name)
                    && Objects.equals(phone, otherCompanyDescriptor.phone)
                    && Objects.equals(email, otherCompanyDescriptor.email)
                    && Objects.equals(role, otherCompanyDescriptor.role)
                    && Objects.equals(deadline, otherCompanyDescriptor.deadline)
                    && Objects.equals(status, otherCompanyDescriptor.status)
                    && Objects.equals(recruiterName, otherCompanyDescriptor.recruiterName)
                    && Objects.equals(tags, otherCompanyDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("role", role)
                    .add("deadline", deadline)
                    .add("status", status)
                    .add("recruiter name", recruiterName)
                    .add("phone", phone)
                    .add("email", email)
                    .add("tags", tags)
                    .toString();
        }
    }
}
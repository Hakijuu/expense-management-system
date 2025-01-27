package com.team.backend.service;

import com.team.backend.exception.*;
import com.team.backend.helpers.DebtsHolder;
import com.team.backend.model.*;
import com.team.backend.repository.ExpenseDetailRepository;
import com.team.backend.repository.UserStatusRepository;
import com.team.backend.repository.WalletUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final WalletService walletService;
    private final UserService userService;
    private final UserStatusRepository userStatusRepository;
    private final ExpenseService expenseService;
    private final ListService listService;
    private final ExpenseDetailRepository expenseDetailRepository;
    private final ListDetailService listDetailService;
    private final MessageService messageService;
    private final WalletUserRepository walletUserRepository;

    public AuthenticationService(WalletService walletService, UserService userService,
                                 UserStatusRepository userStatusRepository, ExpenseService expenseService,
                                 ListService listService, ExpenseDetailRepository expenseDetailRepository,
                                 ListDetailService listDetailService, MessageService messageService,
                                 WalletUserRepository walletUserRepository) {
        this.walletService = walletService;
        this.userService = userService;
        this.userStatusRepository = userStatusRepository;
        this.expenseService = expenseService;
        this.listService = listService;
        this.expenseDetailRepository = expenseDetailRepository;
        this.listDetailService = listDetailService;
        this.messageService = messageService;
        this.walletUserRepository = walletUserRepository;
    }

    public boolean isWalletOwner(int id) {
        Wallet wallet = walletService.findById(id).orElseThrow(WalletNotFoundException::new);
        User currentUser = userService.findCurrentLoggedInUser().orElseThrow(UserNotFoundException::new);
        UserStatus ownerStatus = userStatusRepository.findByName("właściciel")
                .orElseThrow(UserStatusNotFoundException::new);
        WalletUser walletOwnerDetail = wallet.getWalletUserSet().stream()
                .filter(
                        walletUser -> walletUser.getUserStatus().equals(ownerStatus)
                )
                .findFirst().orElseThrow(WalletUserNotFoundException::new);
        User owner = walletOwnerDetail.getUser();

        return owner.equals(currentUser);
    }

    public boolean isCreditor(DebtsHolder debtsHolder) {
        User currentUser = userService.findCurrentLoggedInUser().orElseThrow(UserNotFoundException::new);
        User creditor = debtsHolder.getCreditor();

        return creditor.getLogin().equals(currentUser.getLogin());
    }

    public boolean checkIfMember(Wallet wallet) {
        User currentUser = userService.findCurrentLoggedInUser().orElseThrow(UserNotFoundException::new);
        UserStatus ownerStatus = userStatusRepository.findByName("właściciel")
                .orElseThrow(UserStatusNotFoundException::new);
        UserStatus memberStatus = userStatusRepository.findByName("członek")
                .orElseThrow(UserStatusNotFoundException::new);
        List<WalletUser> walletOwnerDetail = wallet.getWalletUserSet().stream()
                .filter(
                        walletUser ->
                                (walletUser.getUserStatus().equals(ownerStatus)
                                        || walletUser.getUserStatus().equals(memberStatus))
                                        && walletUser.getUser().equals(currentUser)
                ).collect(Collectors.toList());

        return walletOwnerDetail.size() != 0;
    }

    public boolean isWalletMember(int id) {
        Wallet wallet = walletService.findById(id).orElseThrow(WalletNotFoundException::new);

        return checkIfMember(wallet);
    }

    public boolean isInvitationOwner(int id) {
        WalletUser walletUser = walletUserRepository.findById(id).orElseThrow(WalletUserNotFoundException::new);
        User user = walletUser.getUser();
        User currentUser = userService.findCurrentLoggedInUser().orElseThrow(UserNotFoundException::new);

        return user.equals(currentUser);
    }

    public boolean isWalletMemberByExpense(int id) {
        Expense expense = expenseService.findById(id).orElseThrow(ExpenseNotFoundException::new);
        Wallet wallet = expense.getWallet();

        return checkIfMember(wallet);
    }

    public boolean ifExpenseOwner(int id) {
        Expense expense = expenseService.findById(id).orElseThrow(ExpenseNotFoundException::new);
        User expenseOwner = expense.getUser();
        User currentUser = userService.findCurrentLoggedInUser().orElseThrow(UserNotFoundException::new);

        return currentUser.equals(expenseOwner);
    }

    public boolean ifContainsDeletedMembers(int id) {
        Expense expense = expenseService.findById(id).orElseThrow(ExpenseNotFoundException::new);
        Wallet wallet = expense.getWallet();
        List<String> deletedUsersList = walletService.findDeletedUserList(wallet);
        List<String> deletedUsersListInExpense = new ArrayList<>();
        List<ExpenseDetail> expenseDetailList = new ArrayList<>(expense.getExpenseDetailSet());

        for (ExpenseDetail expenseDetail : expenseDetailList)
            if (deletedUsersList.contains(expenseDetail.getUser().getLogin()))
                deletedUsersListInExpense.add(expenseDetail.getUser().getLogin());

        return deletedUsersListInExpense.size() != 0;
    }

    public boolean isWalletMemberByExpenseDetail(int id) {
        ExpenseDetail expenseDetail = expenseDetailRepository.findById(id)
                .orElseThrow(ExpenseDetailNotFoundException::new);
        User user = expenseDetail.getUser();
        User currentUser = userService.findCurrentLoggedInUser().orElseThrow(UserNotFoundException::new);

        return user.equals(currentUser);
    }

    public boolean isWalletMemberByShoppingList(int id) {
        ShoppingList shoppingList = listService.findById(id).orElseThrow(ListNotFoundException::new);
        Wallet wallet = shoppingList.getWallet();

        return checkIfMember(wallet);
    }

    public boolean isWalletMemberByShoppingListDetail(int id) {
        ListDetail shoppingListDetail = listDetailService.findById(id).orElseThrow(ListDetailNotFoundException::new);
        Wallet wallet = shoppingListDetail.getList().getWallet();

        return checkIfMember(wallet);
    }

    public boolean isNotificationOwner(int id) {
        Message notification = messageService.findById(id).orElseThrow(MessageNotFoundException::new);
        User currentUser = userService.findCurrentLoggedInUser().orElseThrow(UserNotFoundException::new);
        User receiver = notification.getReceiver();

        return receiver.equals(currentUser);
    }
}

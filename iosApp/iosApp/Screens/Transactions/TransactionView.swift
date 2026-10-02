import SwiftUI

struct TransactionView: View {
    @Environment(\.finGoalTheme) private var theme

    let transactions: [Transaction]
    let isLoading: Bool

    let onEdit: (Transaction) -> Void
    let onDelete: (Transaction) -> Void

    let onCreateTransaction: (
        String,
        Double,
        String,
        Bool,
        String
    ) -> Void

    let onUpdateTransaction: (
        Transaction,
        String,
        Double,
        String,
        Bool,
        String
    ) -> Void

    @State private var showTransactionSheet = false
    @State private var transactionToEdit: Transaction?

    @State private var transactionToDelete: Transaction?
    @State private var showDeleteDialog = false

    init(
        transactions: [Transaction] = [],
        isLoading: Bool = false,
        onEdit: @escaping (Transaction) -> Void = { _ in },
        onDelete: @escaping (Transaction) -> Void = { _ in },
        onCreateTransaction: @escaping (
            String,
            Double,
            String,
            Bool,
            String
        ) -> Void = { _, _, _, _, _ in },
        onUpdateTransaction: @escaping (
            Transaction,
            String,
            Double,
            String,
            Bool,
            String
        ) -> Void = { _, _, _, _, _, _ in }
    ) {
        self.transactions = transactions
        self.isLoading = isLoading
        self.onEdit = onEdit
        self.onDelete = onDelete
        self.onCreateTransaction = onCreateTransaction
        self.onUpdateTransaction = onUpdateTransaction
    }

    private var transactionsByDate:
        [(String, [Transaction])] {

        let grouped = Dictionary(
            grouping: transactions
        ) {
            $0.date.toFormattedDate()
        }

        return grouped
            .sorted { lhs, rhs in
                lhs.key > rhs.key
            }
    }

    private var totalIncome: Double {
        transactions
            .filter { $0.isIncome }
            .reduce(0) { $0 + $1.amount }
    }

    private var totalExpenses: Double {
        transactions
            .filter { !$0.isIncome }
            .reduce(0) { $0 + $1.amount }
    }

    var body: some View {
        ZStack {
            theme.background
                .ignoresSafeArea()

            ScrollView {
                LazyVStack(
                    alignment: .leading,
                    spacing: 8
                ) {
                    TransactionListHeader(
                        ingresos: totalIncome,
                        gastos: totalExpenses
                    )

                    if isLoading {
                        ProgressView()
                            .frame(
                                maxWidth: .infinity,
                                minHeight: 250
                            )

                    } else if transactions.isEmpty {

                        EmptyStateView(
                            message:
                                "¡No tienes transacciones aún!"
                        )
                        .frame(
                            maxWidth: .infinity
                        )
                        .padding(.top, 48)

                    } else {

                        ForEach(
                            transactionsByDate,
                            id: \.0
                        ) { date, transactions in

                            VStack(
                                alignment: .leading,
                                spacing: 0
                            ) {
                                Text(date)
                                    .font(.title3)
                                    .fontWeight(.medium)
                                    .foregroundStyle(
                                        theme.onSurface
                                    )
                                    .padding(.top, 8)

                                Divider()
                                    .overlay(
                                        theme.outlineVariant
                                    )
                                    .padding(
                                        .vertical,
                                        1
                                    )

                                ForEach(
                                    transactions,
                                    id: \.id
                                ) { transaction in

                                    TransactionItem(
                                        transaction: transaction,
                                        onEdit: {
                                            transactionToEdit =
                                                transaction

                                            showTransactionSheet =
                                                true

                                            onEdit(
                                                transaction
                                            )
                                        },
                                        onDelete: {
                                            transactionToDelete =
                                                transaction

                                            showDeleteDialog =
                                                true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer()
                        .frame(height: 80)
                }
                .padding(16)
            }

            if isLoading {
                ProgressView()
                    .tint(theme.primary)
            }
        }
        .sheet(
            isPresented:
                $showTransactionSheet,
            onDismiss: {
                transactionToEdit = nil
            }
        ) {
            TransactionFormContent(
                initialTransaction:
                    transactionToEdit,
                onSave: {
                    title,
                    amount,
                    category,
                    isIncome,
                    description in

                    if let transaction =
                        transactionToEdit {

                        onUpdateTransaction(
                            transaction,
                            title,
                            amount,
                            category,
                            isIncome,
                            description
                        )

                    } else {

                        onCreateTransaction(
                            title,
                            amount,
                            category,
                            isIncome,
                            description
                        )
                    }

                    showTransactionSheet = false
                    transactionToEdit = nil
                }
            )
            .presentationDetents(
                [.medium, .large]
            )
        }
        .confirmationDialog(
            "¿Eliminar transacción?",
            isPresented:
                $showDeleteDialog,
            titleVisibility: .visible
        ) {
            Button(
                "Eliminar",
                role: .destructive
            ) {
                if let transaction =
                    transactionToDelete {

                    onDelete(transaction)
                }

                transactionToDelete = nil
            }

            Button(
                "Cancelar",
                role: .cancel
            ) {
                transactionToDelete = nil
            }

        } message: {
            Text(
                "¿Seguro que quieres eliminar '\(transactionToDelete?.title ?? "")'?"
            )
        }
    }
}
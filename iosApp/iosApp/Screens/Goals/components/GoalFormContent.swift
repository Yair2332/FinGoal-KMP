import SwiftUI

struct GoalFormContent: View {

    @Environment(\.finGoalTheme) private var theme

    let goal: Goal?

    let onSave: (
        _ title: String,
        _ description: String,
        _ amount: Double,
        _ image: String
    ) -> Void

    @State private var title: String
    @State private var description: String
    @State private var amount: String
    @State private var image: String

    @State private var hasAttemptedSave = false

    init(
        goal: Goal?,
        onSave: @escaping (
            String,
            String,
            Double,
            String
        ) -> Void
    ) {
        self.goal = goal
        self.onSave = onSave

        _title = State(
            initialValue: goal?.title ?? ""
        )

        _description = State(
            initialValue: goal?.description ?? ""
        )

        _amount = State(
            initialValue: goal.map {
                String($0.targetAmount)
            } ?? ""
        )

        _image = State(
            initialValue: goal?.localImagePath ?? ""
        )
    }

    private var parsedAmount: Double? {
        Double(amount)
    }

    private var isAmountValid: Bool {
        guard let parsedAmount else {
            return false
        }

        return parsedAmount > 0
    }

    private var isTitleValid: Bool {
        !title
            .trimmingCharacters(
                in: .whitespacesAndNewlines
            )
            .isEmpty
    }

    private var showError: Bool {
        hasAttemptedSave &&
        (!isTitleValid || !isAmountValid)
    }

    var body: some View {

        ScrollView {

            VStack(
                alignment: .leading,
                spacing: 16
            ) {

                // MARK: Header

                HStack {

                    Image(systemName: "flag.fill")
                        .font(.system(size: 32))
                        .foregroundStyle(
                            theme.primary
                        )

                    Spacer()
                        .frame(width: 12)

                    Text(
                        goal == nil
                            ? "Nueva Meta"
                            : "Editar Meta"
                    )
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundStyle(
                        theme.onSurface
                    )
                }

                // MARK: Title

                HStack {

                    Image(systemName: "pencil")
                        .foregroundStyle(
                            theme.onSurfaceVariant
                        )

                    TextField(
                        "Título",
                        text: $title
                    )
                }
                .padding()
                .background(
                    RoundedRectangle(
                        cornerRadius: 10
                    )
                    .stroke(
                        showError && !isTitleValid
                            ? Color.red
                            : theme.outline.opacity(0.4)
                    )
                )

                Text("Ej: Viaje a Japón")
                    .font(.caption)
                    .foregroundStyle(
                        theme.onSurfaceVariant
                    )

                // MARK: Amount

                HStack {

                    Image(
                        systemName: "dollarsign"
                    )
                    .foregroundStyle(
                        theme.onSurfaceVariant
                    )

                    TextField(
                        "Monto Objetivo ($)",
                        text: $amount
                    )
                    .keyboardType(.decimalPad)
                }
                .padding()
                .background(
                    RoundedRectangle(
                        cornerRadius: 10
                    )
                    .stroke(
                        showError && !isAmountValid
                            ? Color.red
                            : theme.outline.opacity(0.4)
                    )
                )

                // MARK: Image

                HStack {

                    Image(systemName: "photo")
                        .foregroundStyle(
                            theme.onSurfaceVariant
                        )

                    TextField(
                        "URL de imagen (opcional)",
                        text: $image
                    )
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                }
                .padding()
                .background(
                    RoundedRectangle(
                        cornerRadius: 10
                    )
                    .stroke(
                        theme.outline.opacity(0.4)
                    )
                )

                // MARK: Description

                TextField(
                    "Descripción breve",
                    text: $description,
                    axis: .vertical
                )
                .lineLimit(3...3)
                .textFieldStyle(.roundedBorder)

                // MARK: Validation

                if showError {

                    Text(
                        "Por favor, revisa que el título y el monto sean correctos."
                    )
                    .font(.footnote)
                    .foregroundStyle(.red)
                    .padding(.top, 8)
                }

                Spacer()
                    .frame(height: 8)

                // MARK: Save

                Button {

                    hasAttemptedSave = true

                    if isTitleValid,
                       isAmountValid,
                       let parsedAmount {

                        onSave(
                            title,
                            description,
                            parsedAmount,
                            image
                        )
                    }

                } label: {

                    HStack {

                        Image(
                            systemName:
                                "square.and.arrow.down"
                        )

                        Spacer()
                            .frame(width: 8)

                        Text("Guardar Meta")
                            .font(.headline)
                    }
                    .frame(
                        maxWidth: .infinity
                    )
                    .frame(height: 50)
                }
                .buttonStyle(
                    .borderedProminent
                )
                .tint(theme.primary)
                .clipShape(
                    RoundedRectangle(
                        cornerRadius: 12
                    )
                )
            }
            .padding(24)
        }
    }
}
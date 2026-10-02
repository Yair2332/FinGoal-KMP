import SwiftUI

struct AddHabitDialog: View {

    @Environment(\.finGoalTheme) private var theme

    let onDismiss: () -> Void
    let onConfirm: (
        _ name: String,
        _ description: String,
        _ frequency: String
    ) -> Void

    let initialTitle: String
    let initialDescription: String
    let initialFrequency: String

    @State private var title: String
    @State private var description: String
    @State private var frequency: String
    @State private var isError = false

    private let frequencies = [
        "DIARIO",
        "SEMANAL",
        "MENSUAL"
    ]

    init(
        onDismiss: @escaping () -> Void,
        onConfirm: @escaping (
            String,
            String,
            String
        ) -> Void,
        initialTitle: String = "",
        initialDescription: String = "",
        initialFrequency: String = "DIARIO"
    ) {
        self.onDismiss = onDismiss
        self.onConfirm = onConfirm

        self.initialTitle = initialTitle
        self.initialDescription = initialDescription
        self.initialFrequency = initialFrequency

        _title = State(
            initialValue: initialTitle
        )

        _description = State(
            initialValue: initialDescription
        )

        _frequency = State(
            initialValue: initialFrequency
        )
    }

    private var isEditing: Bool {
        !initialTitle.isEmpty
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(
                    alignment: .leading,
                    spacing: 20
                ) {

                    // MARK: - Title

                    HStack {
                        Image(systemName: "calendar")
                            .font(.system(size: 28))
                            .foregroundStyle(theme.primary)

                        Text(
                            isEditing
                            ? "Editar Hábito"
                            : "Nuevo Hábito"
                        )
                        .font(.title2)
                        .fontWeight(.semibold)
                        .foregroundStyle(theme.onSurface)
                    }

                    // MARK: - Name

                    HStack {
                        Image(systemName: "textformat")
                            .foregroundStyle(
                                theme.onSurfaceVariant
                            )

                        TextField(
                            "Nombre del hábito",
                            text: $title
                        )
                        .textInputAutocapitalization(.sentences)
                    }
                    .padding()
                    .background(
                        RoundedRectangle(
                            cornerRadius: 12
                        )
                        .stroke(
                            isError
                            ? Color.red
                            : theme.outline.opacity(0.4)
                        )
                    )

                    // MARK: - Description

                    HStack(alignment: .top) {
                        Image(systemName: "doc.text")
                            .foregroundStyle(
                                theme.onSurfaceVariant
                            )
                            .padding(.top, 3)

                        TextField(
                            "Descripción (Opcional)",
                            text: $description,
                            axis: .vertical
                        )
                        .lineLimit(3...3)
                    }
                    .padding()
                    .background(
                        RoundedRectangle(
                            cornerRadius: 12
                        )
                        .stroke(
                            theme.outline.opacity(0.4)
                        )
                    )

                    // MARK: - Frequency

                    VStack(
                        alignment: .leading,
                        spacing: 8
                    ) {

                        HStack {
                            Image(
                                systemName:
                                    "arrow.triangle.2.circlepath"
                            )
                            .foregroundStyle(theme.primary)

                            Text("Frecuencia")
                                .font(.subheadline)
                                .fontWeight(.semibold)
                                .foregroundStyle(
                                    theme.primary
                                )
                        }

                        HStack(spacing: 4) {

                            ForEach(
                                frequencies,
                                id: \.self
                            ) { freq in

                                let selected =
                                    frequency == freq

                                Button {
                                    withAnimation(
                                        .easeInOut(
                                            duration: 0.15
                                        )
                                    ) {
                                        frequency = freq
                                    }
                                } label: {

                                    Text(
                                        displayFrequency(freq)
                                    )
                                    .font(.caption)
                                    .fontWeight(
                                        selected
                                        ? .bold
                                        : .medium
                                    )
                                    .foregroundStyle(
                                        selected
                                        ? theme.onPrimary
                                        : theme.onSurfaceVariant
                                    )
                                    .frame(
                                        maxWidth: .infinity
                                    )
                                    .frame(height: 44)
                                    .background(
                                        selected
                                        ? theme.primary
                                        : Color.clear
                                    )
                                    .clipShape(
                                        RoundedRectangle(
                                            cornerRadius: 10
                                        )
                                    )
                                }
                            }
                        }
                        .padding(4)
                        .background(
                            theme.surfaceVariant.opacity(0.6)
                        )
                        .clipShape(
                            RoundedRectangle(
                                cornerRadius: 14
                            )
                        )
                    }

                    Spacer()
                        .frame(height: 4)
                }
                .padding(24)
            }
            .navigationTitle("")
            .toolbar {

                ToolbarItem(
                    placement: .cancellationAction
                ) {
                    Button("Cancelar") {
                        onDismiss()
                    }
                    .foregroundStyle(theme.primary)
                }

                ToolbarItem(
                    placement: .confirmationAction
                ) {
                    Button("Guardar") {

                        if title.trimmingCharacters(
                            in: .whitespacesAndNewlines
                        ).isEmpty {

                            isError = true

                        } else {

                            onConfirm(
                                title,
                                description,
                                frequency
                            )
                        }
                    }
                    .fontWeight(.semibold)
                    .foregroundStyle(theme.primary)
                }
            }
        }
        .onChange(of: title) { _, _ in
            isError = false
        }
    }

    private func displayFrequency(
        _ frequency: String
    ) -> String {
        frequency
            .lowercased()
            .prefix(1)
            .uppercased()
        +
        frequency
            .lowercased()
            .dropFirst()
    }
}
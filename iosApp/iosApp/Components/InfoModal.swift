import SwiftUI

struct InfoModalItem: Identifiable {
    let id = UUID()
    let icon: String
    let title: String
    let text: String
}

struct InfoModal: View {
    let imageName: String
    let items: [InfoModalItem]
    let onDismiss: () -> Void

    var body: some View {
        VStack(spacing: 0) {

            Image(imageName)
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity)
                .frame(height: 200)
                .clipped()

            VStack(alignment: .leading, spacing: 0) {
                ForEach(items) { item in
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Image(systemName: item.icon)
                                .foregroundStyle(.tint)

                            Text(item.title)
                                .font(.headline)
                        }

                        Text(item.text)
                            .font(.body)
                            .foregroundStyle(.secondary)
                            .padding(.leading, 32)
                            .padding(.bottom, 8)
                    }
                }

                Button("Entendido") {
                    onDismiss()
                }
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
                .padding(.top, 8)
            }
            .padding(20)
        }
        .background(
            Color(uiColor: .systemBackground)
        )
        .clipShape(
            RoundedRectangle(cornerRadius: 16)
        )
    }
}
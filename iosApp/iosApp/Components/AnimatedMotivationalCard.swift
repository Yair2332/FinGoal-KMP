import SwiftUI

struct AnimatedMotivationalCard: View {

    let imageName: String
    let phrases: [String]
    let shadowColor: Color
    let interval: TimeInterval

    @State private var currentIndex = 0

    init(
        imageName: String,
        phrases: [String],
        shadowColor: Color,
        interval: TimeInterval = 5.0
    ) {
        self.imageName = imageName
        self.phrases = phrases
        self.shadowColor = shadowColor
        self.interval = interval
    }

    var body: some View {
        ZStack {

            Image(imageName)
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity)
                .frame(height: 140)
                .clipShape(
                    RoundedRectangle(
                        cornerRadius: 16
                    )
                )

            LinearGradient(
                colors: [
                    shadowColor.opacity(0.5),
                    shadowColor.opacity(0.7)
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .clipShape(
                RoundedRectangle(
                    cornerRadius: 16
                )
            )

            if !phrases.isEmpty {

                Text(phrases[currentIndex])
                    .foregroundStyle(.white)
                    .font(
                        .system(
                            size: 24,
                            weight: .semibold
                        )
                    )
                    .multilineTextAlignment(.center)
                    .padding(16)
                    .id(currentIndex)
                    .transition(.opacity)
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 140)
        .clipShape(
            RoundedRectangle(
                cornerRadius: 16
            )
        )
        .padding(8)
        .task {

            guard !phrases.isEmpty else {
                return
            }

            while !Task.isCancelled {

                try? await Task.sleep(
                    for: .seconds(interval)
                )

                if !Task.isCancelled {

                    withAnimation(
                        .easeInOut(duration: 1)
                    ) {
                        currentIndex =
                            (currentIndex + 1)
                            % phrases.count
                    }
                }
            }
        }
    }
}
import SwiftUI

struct BalanceChart: View {
    let chartData: [Float]

    var body: some View {
        if chartData.isEmpty {
            EmptyView()
        } else {
            chartContent
        }
    }

    private var data: [Float] {
        [0] + chartData
    }

    private var minValue: Float {
        data.min() ?? 0
    }

    private var maxValue: Float {
        data.max() ?? 1
    }

    private var range: Float {
        max(maxValue - minValue, 1)
    }

    private func formatCurrency(_ value: Float) -> String {
        if value >= 1000 {
            return String(
                format: "%.0fk",
                value / 1000
            )
        }

        return String(
            format: "%.0f",
            value
        )
    }

    private var chartContent: some View {
        HStack {
            VStack {
                Text(formatCurrency(maxValue))
                Text(
                    formatCurrency(
                        (maxValue + minValue) / 2
                    )
                )
                Text(formatCurrency(minValue))
            }
            .font(.system(size: 10))
            .frame(width: 35)
            .frame(maxHeight: .infinity)

            Canvas { context, size in

                let stepX =
                    size.width /
                    CGFloat(max(data.count - 1, 1))

                let gridColor =
                    Color.gray.opacity(0.2)

                // Cuadrícula
                for i in 0...2 {
                    let y =
                        CGFloat(i) *
                        (size.height / 2)

                    var path = Path()

                    path.move(
                        to: CGPoint(
                            x: 0,
                            y: y
                        )
                    )

                    path.addLine(
                        to: CGPoint(
                            x: size.width,
                            y: y
                        )
                    )

                    context.stroke(
                        path,
                        with: .color(gridColor),
                        lineWidth: 1
                    )
                }

                // Línea de tendencia
                if data.count >= 2 {
                    for i in 0..<(data.count - 1) {

                        let startX =
                            CGFloat(i) * stepX

                        let startY =
                            size.height -
                            (
                                CGFloat(
                                    (data[i] - minValue)
                                    / range
                                ) *
                                size.height
                            )

                        let endX =
                            CGFloat(i + 1) * stepX

                        let endY =
                            size.height -
                            (
                                CGFloat(
                                    (data[i + 1] - minValue)
                                    / range
                                ) *
                                size.height
                            )

                        var path = Path()

                        path.move(
                            to: CGPoint(
                                x: startX,
                                y: startY
                            )
                        )

                        path.addLine(
                            to: CGPoint(
                                x: endX,
                                y: endY
                            )
                        )

                        let lineColor =
                            data[i + 1] >= data[i]
                            ? Color(
                                red: 56 / 255,
                                green: 142 / 255,
                                blue: 60 / 255
                            )
                            : Color(
                                red: 211 / 255,
                                green: 47 / 255,
                                blue: 47 / 255
                            )

                        context.stroke(
                            path,
                            with: .color(lineColor),
                            style: StrokeStyle(
                                lineWidth: 6,
                                lineCap: .round
                            )
                        )
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 120)
            .padding(.leading, 4)
        }
    }
}
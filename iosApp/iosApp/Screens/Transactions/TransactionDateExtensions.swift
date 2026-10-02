import Foundation

extension Int64 {
    func toFormattedDate() -> String {
        let date = Date(
            timeIntervalSince1970:
                Double(self) / 1000.0
        )

        let formatter = DateFormatter()
        formatter.locale = Locale(
            identifier: "es_ES"
        )
        formatter.dateFormat = "d 'de' MMMM"

        return formatter.string(from: date)
    }
}
//
//  SwiftZipArchiver.swift
//  NotEnoughProduction
//
//  Created by xharpen on 2021/11/28.
//
//  swiftlint:disable identifier_name
//

import UIKit
import ZIPFoundation
import Shared

class SwiftZipArchiver: ZipArchiver {
    private let archive: Archive

    init(_ archive: Archive) {
        self.archive = archive
    }

    func enumerate(each: @escaping (ZipArchiverZipEntry) -> Void, completionHandler: @escaping (KotlinUnit?, Error?) -> Void) {
        for entry in archive.enumerated() {
            let isDirectory = entry.element.type == .directory
            if isDirectory {
                continue
            }
            do {
                let path = URL(fileURLWithPath: entry.element.path)
                _ = try archive.extract(entry.element) { data in
                    let zipEntry = SwiftZipEntry(path.lastPathComponent, data: data, isDirectory: isDirectory)
                    each(zipEntry)
                }
            } catch {
                completionHandler(nil, nil)
                return
            }
        }

        completionHandler(KotlinUnit(), nil)
    }

    func measureTotalEntryCount(completionHandler: @escaping (KotlinInt?, Error?) -> Void) {
        var count = 0
        for _ in archive.enumerated() {
            count += 1
        }

        completionHandler(KotlinInt(value: Int32(count)), nil)
    }
}

class SwiftZipEntry: ZipArchiverZipEntry {
    let name: String
    let isDirectory: Bool
    let data: Data

    init(_ name: String, data: Data, isDirectory: Bool) {
        self.name = name
        self.isDirectory = isDirectory
        self.data = data
    }

    func extract(to: File) {
        do {
            try data.write(to: to.url, options: .atomic)
        } catch {
            debugPrint(error)
        }
    }
}

extension URL {
   fileprivate var isDirectory: Bool {
       (try? resourceValues(forKeys: [.isDirectoryKey]))?.isDirectory == true
    }
}

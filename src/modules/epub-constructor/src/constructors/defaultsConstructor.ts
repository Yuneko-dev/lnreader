/**
 * Generates an EPUB container XML string.
 *
 * The EPUB container file is used to specify the location of the EPUB package file,
 * which contains the content of the EPUB book.
 *
 * @param fileName - The name of the EPUB package file without the file extension.
 * @returns The EPUB container XML string.
 */
export function defaultContainer(fileName: string) {
  return `<?xml version="1.0" encoding="UTF-8"?>
    <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
      <rootfiles>
        <rootfile full-path="EPUB/${fileName}.opf" media-type="application/oebps-package+xml"/>
      </rootfiles>
    </container>`;
}
/**
 * Generates a default EPUB XML structure with placeholders for metadata, manifest, and spine.
 *
 * @returns {string} The default EPUB XML structure.
 */
export function defaultEpub() {
  return `<?xml version='1.0' encoding='utf-8'?>
    <package xmlns="http://www.idpf.org/2007/opf" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" unique-identifier="BookId" version="3.0">
      <metadata >
        #metadata
      </metadata>
      <manifest>
        <item id="script" href="script.js" media-type="text/javascript"/>
        #manifest
      </manifest>
      <spine toc="ncx">
        #spine
      </spine>
    </package>`;
}
/**
 * Generates the default XML structure for the table of contents (TOC) of an EPUB book.
 *
 * @param chapterLength The length of the book's chapters.
 * @param title The title of the book.
 * @param bookId The unique identifier for the book.
 * @param author The author of the book (optional).
 * @returns The XML string representing the TOC.
 */
export function defaultNcxToc(
  chapterLength: number,
  title: string,
  bookId: string,
  author?: string,
) {
  return `<?xml version="1.0" encoding="UTF-8"?>
  <ncx xmlns:ncx="http://www.daisy.org/z3986/2005/ncx/" xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1" xml:lang="en" dir="ltr">
	<head>
		<meta name="dtb:uid" content="${bookId}" />
		<meta name="dtb:depth" content="${chapterLength}" />
		<meta name="dtb:totalPageCount" content="${chapterLength}" />
		<meta name="dtb:maxPageNumber" content="0" />
	</head>
	<docTitle>
		<text>${title} EPUB</text>
	</docTitle>

	<docAuthor>
		<text>${author ?? ''}</text>
	</docAuthor>

	<navMap>
  #navMap
	</navMap>
</ncx>
`;
}
/**
 * Generates an HTML table of contents (TOC) page with a customizable title.
 *
 * @param title - The title of the TOC page.
 * @returns The HTML string representing the TOC page.
 */
export function defaultHtmlToc(title: string) {
  return `<?xml version="1.0" encoding="utf-8"?>
  <!DOCTYPE html>
  <html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops" lang="en" xml:lang="en">
      <head>
        <title>${title} - TOC</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <link rel="stylesheet" type="text/css" href="styles.css" />
      </head>
      <body>
        <nav epub:type="toc" id="toc">
          <h1>Table of Contents</h1>
          <ol>
            #ol
          </ol>
        </nav>
      </body>
    </html>`;
}

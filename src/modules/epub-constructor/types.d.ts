export interface Parameter {
  name: string;
  value: string;
}

export interface EpubChapter {
  fileName?: string; // use title when is not set
  title: string;
  htmlBody: string;
  parameter?: Parameter[];
}
export interface InternalEpubChapter {
  fileName: string;
  title: string;
  htmlBody: string;
  parameter?: Parameter[];
}

export interface File {
  path: string;
  content: string;
  isImage?: boolean;
}

export interface EpubSettings {
  fileName?: string; // use title when is not set
  title: string;
  language?: string; // Default en
  bookId?: string;
  description?: string;
  source?: string;
  author?: string;
  chapters?: EpubChapter[];
  stylesheet?: string | object;
  parameter?: Parameter[];
  rights?: string;
  cover?: string;
  js?: string;
}

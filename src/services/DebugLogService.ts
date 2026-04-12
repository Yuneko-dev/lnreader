/**
 * DebugLogService - Singleton service that intercepts console.log/warn/error
 * and buffers log entries for display in the Debug Log screen.
 */

export type LogLevel = 'log' | 'warn' | 'error' | 'info';

export interface LogEntry {
  id: number;
  timestamp: Date;
  level: LogLevel;
  message: string;
}

type LogSubscriber = (entries: LogEntry[]) => void;

const MAX_ENTRIES = 500;

class DebugLogServiceClass {
  private entries: LogEntry[] = [];
  private nextId = 0;
  private subscribers = new Set<LogSubscriber>();
  private installed = false;

  // Store original console methods
  private originalConsole = {
    log: console.log,
    warn: console.warn,
    error: console.error,
    info: console.info,
  };

  /**
   * Install console interceptors. Safe to call multiple times.
   */
  install() {
    if (this.installed) {
      return;
    }
    this.installed = true;

    const self = this;

    console.log = (...args: unknown[]) => {
      self.originalConsole.log(...args);
      self.addEntry('log', self.formatArgs(args));
    };

    console.warn = (...args: unknown[]) => {
      self.originalConsole.warn(...args);
      self.addEntry('warn', self.formatArgs(args));
    };

    console.error = (...args: unknown[]) => {
      self.originalConsole.error(...args);
      self.addEntry('error', self.formatArgs(args));
    };

    console.info = (...args: unknown[]) => {
      self.originalConsole.info(...args);
      self.addEntry('info', self.formatArgs(args));
    };
  }

  private formatArgs(args: unknown[]): string {
    return args
      .map(arg => {
        if (typeof arg === 'string') {
          return arg;
        }
        try {
          return JSON.stringify(arg, null, 2);
        } catch {
          return String(arg);
        }
      })
      .join(' ');
  }

  /**
   * Add a log entry directly (useful for backup/restore logging).
   */
  addEntry(level: LogLevel, message: string) {
    const entry: LogEntry = {
      id: this.nextId++,
      timestamp: new Date(),
      level,
      message,
    };

    this.entries.push(entry);

    // Trim to max entries
    if (this.entries.length > MAX_ENTRIES) {
      this.entries = this.entries.slice(-MAX_ENTRIES);
    }

    this.notifySubscribers();
  }

  /**
   * Get all current entries.
   */
  getEntries(): LogEntry[] {
    return [...this.entries];
  }

  /**
   * Clear all entries.
   */
  clear() {
    this.entries = [];
    this.notifySubscribers();
  }

  /**
   * Get the next ID that will be assigned (useful for session tracking).
   */
  getNextId(): number {
    return this.nextId;
  }

  /**
   * Subscribe to log changes.
   */
  subscribe(callback: LogSubscriber): () => void {
    this.subscribers.add(callback);
    return () => {
      this.subscribers.delete(callback);
    };
  }

  private notifySubscribers() {
    const entries = this.getEntries();
    this.subscribers.forEach(cb => cb(entries));
  }
}

const DebugLogService = new DebugLogServiceClass();
export default DebugLogService;

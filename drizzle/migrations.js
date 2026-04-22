// This file is required for Expo/React Native SQLite migrations - https://orm.drizzle.team/quick-sqlite/expo

import m0000 from './20251222152612_past_mandrill/migration.sql';
import m0001 from './20260412000000_add_read_duration/migration.sql';
import m0002 from './20260423000000_add_date_fetch/migration.sql';

export default {
  journal: { entries: [] },
  migrations: {
    '20251222152612_past_mandrill': m0000,
    '20260412000000_add_read_duration': m0001,
    '20260423000000_add_date_fetch': m0002,
  },
};

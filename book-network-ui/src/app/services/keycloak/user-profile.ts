export interface UserProfile {
  username?: string;
  email?: string;
  name?: string;
  firstName?: string;
  lastName?: string;
  token?: string;
  tokenParsed?: { [key: string]: unknown };
}

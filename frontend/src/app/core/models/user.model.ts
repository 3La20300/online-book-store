export type Role = 'USER' | 'ADMIN';

export interface User {
  id: number;
  email: string;
  phone: string;
  role: Role;
}

export interface User {
  id: number;
  username: string;
  password: string;
  email: string;
  gender: string;
  age: number;
  profession: string;
  hobby: string;
  bio: string;
  avatar: string;
  role: string;
  lastMessage?: {
    content: string;
    time: string;
  };
  isOnline?: boolean;
}

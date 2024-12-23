export interface Post {
  id: number;
  title: string;
  content: string;
  sectionId: number;
  sectionTitle: string;
  authorId: number;
  author: string;
  avatar: string;
  createdTime: string;
  lastReplyTime: string;
}

export interface Book {
  id: number;
  title: string;
  author: string;
  category: string;
  price: number;
  description: string | null;
  imageUrl: string | null;
}

export type BookRequest = Omit<Book, 'id'>;

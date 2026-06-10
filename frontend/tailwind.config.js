/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        cream: '#FFF8F0',
        creamDeep: '#FFF7ED',
        primary: '#FF7A59',
        secondary: '#FFD166',
        point: '#7C3AED',
        success: '#22C55E',
        warning: '#F59E0B',
        danger: '#EF4444',
        ink: '#1F2937',
      },
      boxShadow: {
        soft: '0 24px 60px rgba(124, 58, 237, 0.14)',
        pop: '0 18px 36px rgba(255, 122, 89, 0.24)',
        button: '0 12px 26px rgba(255, 122, 89, 0.34)',
      },
      borderRadius: {
        card: '32px',
      },
      fontFamily: {
        sans: ['Pretendard', 'Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};

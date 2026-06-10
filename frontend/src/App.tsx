import { Navigate, Route, Routes } from 'react-router-dom';
import AppHeader from './components/AppHeader';
import Home from './pages/Home';
import ClassDetail from './pages/ClassDetail';
import QueueRoom from './pages/QueueRoom';
import ApplicationHold from './pages/ApplicationHold';
import ApplicationComplete from './pages/ApplicationComplete';
import MyApplications from './pages/MyApplications';

export default function App() {
  return (
    <Routes>
      <Route element={<AppHeader />}>
        <Route path="/" element={<Home />} />
        <Route path="/classes/:id" element={<ClassDetail />} />
        <Route path="/queue/:classId" element={<QueueRoom />} />
        <Route path="/apply/:classId" element={<ApplicationHold />} />
        <Route path="/complete/:applicationId" element={<ApplicationComplete />} />
        <Route path="/my" element={<MyApplications />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}

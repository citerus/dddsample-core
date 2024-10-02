import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./App.css";
import Track from "./pages/Track";
import Error from "./pages/Error";
import List from "./pages/admin/List";
import Registration from "./pages/admin/Registration";
import AdminLayout from "./pages/admin/AdminLayout";
import AdminDetails from "./pages/admin/Details";
import ChangeRoute from "./pages/admin/ChangeRoute";
import Home from "./pages/Home";
import RootLayout from "./pages/RootLayout";
const router = createBrowserRouter([
  {
    path: "/dddsample/",
    errorElement: <Error />,
    element: <RootLayout />,
    children: [
      {
        path: "/dddsample/",
        element: <Home />,
        children: [
          {
            path: "/dddsample/cargo/:trackingId",
            element: <Track />,
          },
        ],
      },
      {
        path: "/dddsample/admin/",
        element: <AdminLayout />,
        children: [
          {
            path: "/dddsample/admin/cargo",
            element: <List />,
          },
          {
            path: "/dddsample/admin/cargo/:trackingId",
            element: <AdminDetails />,
          },
          {
            path: "/dddsample/admin/cargo/:trackingId/route",
            element: <ChangeRoute />,
          },
          {
            path: "/dddsample/admin/registration",
            element: <Registration />,
          },
        ],
      },
    ],
  },
  {
    path: "*",
    element: <Error />,
  },
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;

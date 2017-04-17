defmodule Balancer.Router do
  use Sugar.Router
  plug Sugar.Plugs.HotCodeReload

  if Sugar.Config.get(:sugar, :show_debugger, false) do
    use Plug.Debugger, otp_app: :balancer
  end

  plug Plug.Static, at: "/static", from: :balancer

  # Uncomment the following line for session store
  # plug Plug.Session, store: :ets, key: "sid", secure: true, table: :session

  # Define your routes here
  get "/", Balancer.Controllers.Main, :index
end

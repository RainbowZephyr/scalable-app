defmodule Balancer.Controllers.Main do
  use Sugar.Controller

  def index(conn, []) do
    raw conn |> resp(200, "Hello world")
  end

  def get_json(conn, []) do
  IO.puts("entered")
      json conn, %{message: "foobar"}
  end

end

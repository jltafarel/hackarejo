class StaticPagesController < ApplicationController
  skip_authorization_check

  def index
    @title = "Fidelis"
  end
end
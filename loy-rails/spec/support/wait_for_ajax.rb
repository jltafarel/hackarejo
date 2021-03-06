module WaitForAjax
  def wait_for_ajax
    Timeout.timeout(Capybara.default_max_wait_time) do
      loop until finished_all_ajax_requests?
    end
  end

  def finished_all_ajax_requests?
    page.evaluate_script('jQuery.active').try(:zero?)
  end

  def wait_for_turbolinks
    sleep 0.5 # Find better solution
  end
end

RSpec.configure do |config|
  config.include WaitForAjax, type: :feature
end
